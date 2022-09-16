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

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;

class FutureObserverTest {

    @Example
    void create() {
        final ScheduledFuture<?> scheduledFuture = Mockito.mock(ScheduledFuture.class);
        Assertions
                .assertThat(FutureObserver.create(scheduledFuture, "threadName"))
                .isInstanceOf(Thread.class);
    }

    @SuppressWarnings("unused")
    @Provide
    Arbitrary<Exception> exceptions() {
        return Arbitraries.of(
                new InterruptedException(),
                new CancellationException()
        );
    }

    @Property
    void createRunnableNormalFinishByException(@ForAll("exceptions") final Exception exception) throws ExecutionException, InterruptedException, TimeoutException {
        final ScheduledFuture<?> scheduledFuture = Mockito.mock(ScheduledFuture.class);
        Mockito
                .when(scheduledFuture.get(Mockito.anyLong(), Mockito.any()))
                .thenThrow(exception);
        final Logger logger = Mockito.mock(Logger.class);
        FutureObserver.createRunnable(scheduledFuture, logger).run();

        Mockito.verify(logger, Mockito.times(2)).info(Mockito.anyString());
        Mockito.verify(logger).trace("Getting scheduledFuture now.");
        Mockito.verifyNoMoreInteractions(logger);
    }

    @Example
    void createRunnableOneTimeout() throws ExecutionException, InterruptedException, TimeoutException {
        final ScheduledFuture<?> scheduledFuture = Mockito.mock(ScheduledFuture.class);
        Mockito
                .when(scheduledFuture.get(Mockito.anyLong(), Mockito.any()))
                .thenThrow(new TimeoutException())
                .thenThrow(new CancellationException());
        final Logger logger = Mockito.mock(Logger.class);

        FutureObserver.createRunnable(scheduledFuture, logger).run();

        Mockito.verify(logger, Mockito.times(2)).info(Mockito.anyString());
        Mockito.verify(logger, Mockito.times(3)).trace(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(logger);
    }

    @Example
    void createRunnableExecutionException() throws ExecutionException, InterruptedException, TimeoutException {
        final ScheduledFuture<?> scheduledFuture = Mockito.mock(ScheduledFuture.class);
        Mockito
                .when(scheduledFuture.get(Mockito.anyLong(), Mockito.any()))
                .thenThrow(new ExecutionException("Test", new Exception("cause")))
                .thenThrow(new CancellationException());
        final Logger logger = Mockito.mock(Logger.class);

        FutureObserver.createRunnable(scheduledFuture, logger).run();

        Mockito.verify(logger).error(Mockito.anyString(), (Exception) Mockito.any());
        Mockito.verify(logger, Mockito.times(2)).info(Mockito.anyString());
        Mockito.verify(logger, Mockito.times(2)).trace(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(logger);
    }
}