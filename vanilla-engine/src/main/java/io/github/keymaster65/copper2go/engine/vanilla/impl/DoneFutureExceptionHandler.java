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
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

class DoneFutureExceptionHandler<T> {

    private static final Logger log = LoggerFactory.getLogger(DoneFutureExceptionHandler.class);

    private final Map<Future<?>, T> observables;

    DoneFutureExceptionHandler(final Map<Future<?>, T> observables) {
        this.observables = observables;
    }

    void handleDone() {
        observables
                .keySet()
                .stream()
                .filter(Future::isDone)
                .forEach(voidFuture -> getAndRemove(voidFuture, log));
    }

    Optional<T> getAndRemove(final Future<?> voidFuture, final Logger logger) {
        final T value = observables.remove(voidFuture);
        logger.info("Removed value {}.", value);
        final Optional<T> optionalValue = Optional.ofNullable(value);
        try {
            voidFuture.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("InterruptedException caught in future {}.", value, e);
        } catch (ExecutionException e) {
            logger.warn("Exception caught in future {}.", value, e);
        }

        return optionalValue;
    }
}
