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

import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.api.workflow.EventChannelStore;
import io.github.keymaster65.copper2go.api.workflow.RequestChannelStore;
import io.github.keymaster65.copper2go.engine.ReplyChannelStoreImpl;
import io.github.keymaster65.copper2go.engine.vanilla.workflowapi.Workflow;
import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

class EngineControlImplTest {

    @Example
    void start() throws EngineException {
        @SuppressWarnings("unchecked") final FutureStore<Continuation> continuationStore = Mockito.mock(FutureStore.class);
        final VanillaEngineImpl engine = new VanillaEngineImpl(
                Mockito.mock(ReplyChannelStoreImpl.class),
                Mockito.mock(RequestChannelStore.class),
                Mockito.mock(EventChannelStore.class),
                Mockito.mock(ExecutorService.class),
                continuationStore,
                Mockito.mock(ExpectedResponsesStore.class)
        );
        @SuppressWarnings("unchecked") final FutureStore<Workflow> workflowStore = Mockito.mock(FutureStore.class);

        try (EngineControlImpl engineControl = new EngineControlImpl(
                engine,
                workflowStore,
                continuationStore
        )) {
            engineControl.start();
        }

        Mockito.verify(workflowStore).start();
        Mockito.verify(continuationStore).start();
    }

    @Example
    void stop() throws EngineException {
        @SuppressWarnings("unchecked") final FutureStore<Continuation> continuationStore = Mockito.mock(FutureStore.class);
        final VanillaEngineImpl engine = new VanillaEngineImpl(
                Mockito.mock(ReplyChannelStoreImpl.class),
                Mockito.mock(RequestChannelStore.class),
                Mockito.mock(EventChannelStore.class),
                Mockito.mock(ExecutorService.class),
                continuationStore,
                Mockito.mock(ExpectedResponsesStore.class)
        );
        @SuppressWarnings("unchecked") final FutureStore<Workflow> workflowStore = Mockito.mock(FutureStore.class);
        Mockito.when(workflowStore.size()).thenReturn(1L).thenReturn(1L).thenReturn(0L);
        Mockito.when(continuationStore.size()).thenReturn(1L).thenReturn(0L);

        try (EngineControlImpl engineControl = new EngineControlImpl(
                engine,
                workflowStore,
                continuationStore
        )) {
            engineControl.start();
        }

        Mockito.verify(workflowStore).stop();
        Mockito.verify(workflowStore, Mockito.atLeast(3)).size();
        Mockito.verify(continuationStore).stop();
        Mockito.verify(continuationStore, Mockito.atLeast(2)).size();
    }

    @Example
    void startMissingExecutorServiceException() {
        @SuppressWarnings("unchecked") final FutureStore<Continuation> continuationStore = Mockito.mock(FutureStore.class);
        @SuppressWarnings("unchecked") final FutureStore<Workflow> workflowStore = Mockito.mock(FutureStore.class);
        @SuppressWarnings("resource") final EngineControlImpl engineControl =
                new EngineControlImpl(
                        Mockito.mock(VanillaEngineImpl.class),
                        workflowStore,
                        continuationStore
                );


        Assertions
                .assertThatCode(engineControl::start)
                .isInstanceOf(EngineException.class);
    }

    @Example
    void stopMissingExecutorServiceException() {
        @SuppressWarnings("unchecked") final FutureStore<Continuation> continuationStore = Mockito.mock(FutureStore.class);
        @SuppressWarnings("unchecked") final FutureStore<Workflow> workflowStore = Mockito.mock(FutureStore.class);
        @SuppressWarnings("resource") final EngineControlImpl engineControl =
                new EngineControlImpl(
                        Mockito.mock(VanillaEngineImpl.class),
                        workflowStore,
                        continuationStore
                );


        Assertions
                .assertThatCode(engineControl::stop)
                .isInstanceOf(EngineException.class);
    }
}