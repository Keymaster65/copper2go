/*
 * Copyright 2021 Wolf Sluyterman van Langeweyde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.keymaster65.copper2go.engine.vanilla;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureObserver {

    private static final Logger log = LoggerFactory.getLogger(FutureObserver.class);

    static Thread create(ScheduledFuture<?> scheduledFuture, final String threadName) {
        return new Thread(createRunnable(scheduledFuture), threadName);
    }

    private static Runnable createRunnable(ScheduledFuture<?> scheduledFuture) {
        // TODO improve messages
        return () -> {
            log.info("Start scheduledFuture.");
            boolean canceled = false;
            while (!canceled) {
                try {
                    log.trace("Getting scheduledFuture now.");
                    scheduledFuture.get(3, TimeUnit.SECONDS);
                } catch (ExecutionException e) {
                    log.error("Ignore exception while getting scheduledFuture.", e);
                } catch (TimeoutException e) {
                    log.trace("Ignore timeout while getting scheduledFuture.");
                } catch (CancellationException e) {
                    log.info("Cancel scheduledFuture now due to cancellation.");
                    canceled = true;
                } catch (InterruptedException e) {
                    log.info("Cancel scheduledFuture now due to interruption.");
                    canceled = true;
                    Thread.currentThread().interrupt();
                }
            }
        };
    }

    private FutureObserver() {
    }
}
