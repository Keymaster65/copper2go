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
package io.github.keymaster65.copper2go.engine.vanilla.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FutureStore<T> {

    public static final long INITIAL_DELAY = 0;
    public static final long PERIOD = 500;
    public static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    private final Map<Future<?>, T> instances;
    private final ScheduledExecutorService futureHandlerService;
    private final DoneFutureExceptionHandler<T> doneFutureExceptionHandler;
    private static final Logger log = LoggerFactory.getLogger(FutureStore.class);
    private final Class<T> type;

    public FutureStore(final Class<T> type) {
        this(
                Executors.newSingleThreadScheduledExecutor(),
                new ConcurrentHashMap<>(),
                type
        );
    }

    FutureStore(
            final ScheduledExecutorService futureHandlerService,
            final ConcurrentHashMap<Future<?>, T> instances,
            final Class<T> type
    ) {
        this(futureHandlerService, instances, new DoneFutureExceptionHandler<>(instances), type);
    }

    FutureStore(
            final ScheduledExecutorService futureHandlerService,
            final Map<Future<?>, T> instances,
            final DoneFutureExceptionHandler<T> doneFutureExceptionHandler,
            final Class<T> type
    ) {
        this.futureHandlerService = futureHandlerService;
        this.instances = instances;
        this.doneFutureExceptionHandler = doneFutureExceptionHandler;
        this.type = type;
    }

    public synchronized void start() {
        final ScheduledFuture<?> scheduledFuture = futureHandlerService.scheduleAtFixedRate(
                doneFutureExceptionHandler::handleDone,
                INITIAL_DELAY,
                PERIOD,
                TIME_UNIT
        );
        FutureObserver.create(scheduledFuture, getThreadName()).start();
    }

    public synchronized void stop() {
        futureHandlerService.shutdown();
    }

    public void addFuture(final Future<?> future, final T instance) {
        log.debug("Add {}} instance {}.", type.getSimpleName(), instance);
        instances.put(future, instance);
    }

    public long size() {
        return instances.size();
    }

    String getThreadName() {
        return type.getSimpleName() + "Observer";
    }
}
