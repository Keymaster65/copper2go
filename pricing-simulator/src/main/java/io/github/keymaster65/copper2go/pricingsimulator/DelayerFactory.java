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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

class DelayerFactory {
    enum Mode {
        PARK,
        SLEEP,
        WAIT,
        TRY_LOCK,
        POLL
    }

    private static final BlockingQueue<String> queue = new SynchronousQueue<>();
    private static final Lock lock = new ReentrantLock();
    private static final Logger log = LoggerFactory.getLogger(DelayerFactory.class);

    static {
        final Thread thread = new Thread(lock::lock);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread join was interrupted.", e);
        }
    }

    static Delayer create(final Mode delayMode) {
        return switch (delayMode) {
            case PARK -> DelayerFactory::park;
            case SLEEP -> DelayerFactory::sleep;
            case WAIT -> DelayerFactory::wait;
            case TRY_LOCK -> DelayerFactory::tryLock;
            case POLL -> DelayerFactory::poll;
        };
    }

    private static void park(final Duration delay) {
        log.debug("Start parking for {}", delay);
        LockSupport.parkNanos(delay.toNanos());
        log.debug("Stop parking for {}", delay);
    }

    private static void sleep(final Duration delay) throws InterruptedException {
        log.debug("Start sleeping for {}", delay);
        Thread.sleep(delay);
        log.debug("Stop sleeping for {}", delay);
    }

    private static void wait(final Duration delay) throws InterruptedException {
        final Object lock = new Object();
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (lock) {
            log.debug("Start waiting for {}", delay);
            lock.wait(delay.toMillis()); // NOSONAR: Demo code only
            log.debug("Stop waiting for {}", delay);
        }
    }

    private static void tryLock(final Duration delay) throws InterruptedException {
        log.debug("Start tryLock for {}", delay);
        //noinspection ResultOfMethodCallIgnored
        lock.tryLock(delay.toMillis(), TimeUnit.MILLISECONDS);
        log.debug("Stop tryLock for {}", delay);
    }

    private static void poll(final Duration delay) throws InterruptedException {
        log.debug("Start poll for {}", delay);
        queue.poll(delay.toMillis(), TimeUnit.MILLISECONDS);
        log.debug("Stop poll for {}", delay);
    }
}
