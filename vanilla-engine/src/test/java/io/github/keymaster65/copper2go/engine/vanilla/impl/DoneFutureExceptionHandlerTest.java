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

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

class DoneFutureExceptionHandlerTest {

    @Example
    void get() throws ExecutionException, InterruptedException {
        final var observables = new ConcurrentHashMap<Future<?>, Object>();
        final var futureHandler = new DoneFutureExceptionHandler<>(observables);
        final Future<?> future = Mockito.mock(Future.class);
        final Logger logger = Mockito.mock(Logger.class);
        final Object value = new Object();
        observables.put(future, value);

        final Optional<Object> result = futureHandler.getAndRemove(future, logger);

        Assertions.assertThat(result.orElseThrow()).isSameAs(value);
        Assertions.assertThat(observables).isEmpty();
        Mockito.verify(future).get();
        Mockito.verify(logger).info(Mockito.anyString(), Mockito.eq(value));
        Mockito.verifyNoMoreInteractions(logger);
    }

    @Example
    void getRemoveFail() throws ExecutionException, InterruptedException {
        final var observables = new ConcurrentHashMap<Future<?>, Object>();
        final var futureHandler = new DoneFutureExceptionHandler<>(observables);
        final Future<?> future = Mockito.mock(Future.class);
        final Logger logger = Mockito.mock(Logger.class);

        final Optional<Object> result = futureHandler.getAndRemove(future, logger);

        Assertions.assertThat(result).isEmpty();
        Assertions.assertThat(observables).isEmpty();
        Mockito.verify(future).get();
        Mockito.verify(logger).info(Mockito.anyString(), (Object) Mockito.eq(null));
        Mockito.verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("unused")
    @Provide
    final Arbitrary<Exception> exceptions() {
        return Arbitraries.of(
                new ExecutionException("Test", new Exception("Test cause.")),
                new InterruptedException("Test")
        );
    }
    @Property
    void getExecutionException(@ForAll("exceptions") Exception exception) throws ExecutionException, InterruptedException {
        final var observables = new ConcurrentHashMap<Future<?>, Object>();
        final var futureHandler = new DoneFutureExceptionHandler<>(observables);
        final Future<?> future = Mockito.mock(Future.class);
        Mockito.when(future.get()).thenThrow(exception);
        final Logger logger = Mockito.mock(Logger.class);
        final Object value = new Object();
        observables.put(future, value);

        final Optional<Object> result = futureHandler.getAndRemove(future, logger);

        Assertions.assertThat(observables).isEmpty();
        Assertions.assertThat(result.orElseThrow()).isSameAs(value);
        Mockito.verify(future).get();
        Mockito.verify(logger).info(Mockito.anyString(), Mockito.eq(value));
        Mockito.verify(logger).warn(Mockito.anyString(), Mockito.eq(value), Mockito.eq(exception));
        Mockito.verifyNoMoreInteractions(logger);
    }

    @Example
    void handleDone() throws ExecutionException, InterruptedException {
        final var observables = new ConcurrentHashMap<Future<?>, Object>();
        final var futureHandler = new DoneFutureExceptionHandler<>(observables);
        final Future<?> future = Mockito.mock(Future.class);
        Mockito.when(future.isDone()).thenReturn(true);
        final Object value = new Object();
        observables.put(future, value);

        futureHandler.handleDone();

        Assertions.assertThat(observables).isEmpty();
        Mockito.verify(future).get();
    }

    @Example
    void handleIgnoreUnDone() {
        final var observables = new ConcurrentHashMap<Future<?>, Object>();
        final var futureHandler = new DoneFutureExceptionHandler<>(observables);
        final Future<?> future = Mockito.mock(Future.class);
        final Object value = new Object();
        observables.put(future, value);

        futureHandler.handleDone();

        Assertions.assertThat(observables).containsKey(future);
    }
}