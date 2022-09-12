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

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ExecutorServices {

    private static final Logger log = LoggerFactory.getLogger(ExecutorServices.class);

    public static ExecutorService start() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    public static boolean stop(final ExecutorService executorService) {
        log.debug("Start parking.");
        LockSupport.parkNanos(Duration.ofSeconds(10).toNanos());
        log.debug("End parking.");
        executorService.shutdown();
        try {
            return executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Ignore InterruptedException.", e);
            final boolean ignored = Thread.currentThread().isInterrupted();
        }
        return false;
    }
}
