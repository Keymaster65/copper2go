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
package io.github.keymaster65.copper2go.pricingsimulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.locks.LockSupport;

class DelayerFactory {
    enum Mode {
        PARK,
        SLEEP,
        WAIT
    }

    private static final Logger log = LoggerFactory.getLogger(DelayerFactory.class);

    static Delayer create(final Mode delayMode) {
        return switch (delayMode)  {
            case PARK -> DelayerFactory::park;
            case SLEEP -> DelayerFactory::sleep;
            case WAIT -> DelayerFactory::wait;
        };
    }

    private static void park(final Duration delay) {
        log.debug("Start parking for {}", delay);
        LockSupport.parkNanos(delay.toNanos());
        log.debug("Stop parking for {}", delay);
    }

    private static void sleep(final Duration delay) {
        log.debug("Start sleeping for {}", delay);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            log.debug("Interrupted sleeping for {}", delay, e);
            Thread.currentThread().interrupt();
        }
        log.debug("Stop sleeping for {}", delay);
    }

    private static void wait(final Duration delay) {
        final Object lock = new Object();
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (lock) {
            try {
                log.debug("Start waiting for {}", delay);
                lock.wait(delay.toMillis()); // NOSONAR: Demo code only
            } catch (InterruptedException e) {
                log.debug("Interrupted waiting for {}", delay, e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
