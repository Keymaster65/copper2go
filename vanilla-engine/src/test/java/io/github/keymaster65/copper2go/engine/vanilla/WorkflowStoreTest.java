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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

class WorkflowStoreTest {

    @Test
    void start() {
        final ScheduledExecutorService futureHandlerService = Mockito.mock(ScheduledExecutorService.class);
        @SuppressWarnings("unchecked") final WorkflowStore workflowStore = new WorkflowStore(
                futureHandlerService,
                Mockito.mock(ConcurrentHashMap.class)
        );
        @SuppressWarnings("rawtypes") final ScheduledFuture future = Mockito.mock(ScheduledFuture.class);
        //noinspection unchecked
        Mockito
                .when(
                        futureHandlerService.scheduleAtFixedRate(
                                Mockito.any(),
                                Mockito.eq(WorkflowStore.INITIAL_DELAY),
                                Mockito.eq(WorkflowStore.PERIOD),
                                Mockito.eq(WorkflowStore.TIME_UNIT)
                        )
                )
                .thenReturn(future);

        workflowStore.start();

        Mockito.verify(futureHandlerService).scheduleAtFixedRate(
                Mockito.any(),
                Mockito.eq(WorkflowStore.INITIAL_DELAY),
                Mockito.eq(WorkflowStore.PERIOD),
                Mockito.eq(WorkflowStore.TIME_UNIT)
        );
    }

    @Test
    void stop() {
        final ScheduledExecutorService futureHandlerService = Mockito.mock(ScheduledExecutorService.class);
        @SuppressWarnings("unchecked") final WorkflowStore workflowStore = new WorkflowStore(
                futureHandlerService,
                Mockito.mock(ConcurrentHashMap.class)
        );

        workflowStore.stop();

        Mockito.verify(futureHandlerService).shutdown();
    }

    @Test
    void addFuture() {
        final WorkflowStore workflowStore = new WorkflowStore();

        workflowStore.addFuture(Mockito.mock(Future.class), Mockito.mock(Workflow.class));

        Assertions.assertThat(workflowStore.getWorkflowInstanceCount()).isOne();
    }

    @Test
    void getWorkflowInstanceCount() {
        final WorkflowStore workflowStore = new WorkflowStore();

        Assertions.assertThat(workflowStore.getWorkflowInstanceCount()).isZero();
    }
}