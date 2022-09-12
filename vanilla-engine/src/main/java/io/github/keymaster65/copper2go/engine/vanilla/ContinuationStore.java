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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ContinuationStore {

    public static final long INITIAL_DELAY = 0;
    public static final long PERIOD = 500;
    public static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    private final Map<Future<?>, Continuation> continuations;
    private final ScheduledExecutorService newSingleThreadScheduledExecutor;
    private final DoneFutureExceptionHandler<Continuation> doneFutureExceptionHandler;

    private static final Logger log = LoggerFactory.getLogger(ContinuationStore.class);

    ContinuationStore(final Map<String, Continuation> store) {
        this(
                store,
                Executors.newSingleThreadScheduledExecutor(),
                new ConcurrentHashMap<>()
        );
    }

    ContinuationStore(
            final Map<String, Continuation> store,
            final ScheduledExecutorService newSingleThreadScheduledExecutor,
            final ConcurrentHashMap<Future<?>, Continuation> continuations
    ) {
        this(
                store,
                newSingleThreadScheduledExecutor,
                continuations,
                new DoneFutureExceptionHandler<>(continuations)
        );
    }

    ContinuationStore(
            final Map<String, Continuation> expectedResponses,
            final ScheduledExecutorService newSingleThreadScheduledExecutor,
            final Map<Future<?>, Continuation> continuations,
            final DoneFutureExceptionHandler<Continuation> doneFutureExceptionHandler
    ) {
        this.newSingleThreadScheduledExecutor = newSingleThreadScheduledExecutor;
        this.continuations = continuations;
        this.doneFutureExceptionHandler = doneFutureExceptionHandler;
    }


    public void start() {
        final ScheduledFuture<?> scheduledFuture = newSingleThreadScheduledExecutor.scheduleAtFixedRate(
                doneFutureExceptionHandler::handleDone,
                INITIAL_DELAY,
                PERIOD,
                TIME_UNIT
        );
        FutureObserver.create(
                scheduledFuture,
                "ContinuationObserver"
        ).start();
    }

    public void stop() {
        newSingleThreadScheduledExecutor.shutdown();
    }

    public void addFuture(final Future<?> continuationFuture, final Continuation continuation) {
        log.debug("Add continuation instance {}.", continuation);
        continuations.put(continuationFuture, continuation);
    }

    public long getActiveContinuationsCount() {
        return continuations.size();
    }

}
