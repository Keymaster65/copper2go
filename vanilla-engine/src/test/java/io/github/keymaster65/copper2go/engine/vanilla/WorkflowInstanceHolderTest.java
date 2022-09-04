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

class WorkflowInstanceHolderTest {

    @Test
    void start() {
        final ScheduledExecutorService futureHandlerService = Mockito.mock(ScheduledExecutorService.class);
        @SuppressWarnings("unchecked") final WorkflowInstanceHolder workflowInstanceHolder = new WorkflowInstanceHolder(
                futureHandlerService,
                Mockito.mock(ConcurrentHashMap.class)
        );

        workflowInstanceHolder.start();

        Mockito.verify(futureHandlerService).scheduleAtFixedRate(
                Mockito.any(),
                Mockito.eq(WorkflowInstanceHolder.INITIAL_DELAY),
                Mockito.eq(WorkflowInstanceHolder.PERIOD),
                Mockito.eq(WorkflowInstanceHolder.TIME_UNIT)
        );
    }

    @Test
    void stop() {
        final ScheduledExecutorService futureHandlerService = Mockito.mock(ScheduledExecutorService.class);
        @SuppressWarnings("unchecked") final WorkflowInstanceHolder workflowInstanceHolder = new WorkflowInstanceHolder(
                futureHandlerService,
                Mockito.mock(ConcurrentHashMap.class)
        );

        workflowInstanceHolder.stop();

        Mockito.verify(futureHandlerService).shutdown();
    }

    @Test
    void addFuture() {
        final WorkflowInstanceHolder workflowInstanceHolder = new WorkflowInstanceHolder();

        workflowInstanceHolder.addFuture(Mockito.mock(Future.class), Mockito.mock(Workflow.class));

        Assertions.assertThat(workflowInstanceHolder.getWorkflowInstanceCount()).isOne();
    }

    @Test
    void getWorkflowInstanceCount() {
        final WorkflowInstanceHolder workflowInstanceHolder = new WorkflowInstanceHolder();

        Assertions.assertThat(workflowInstanceHolder.getWorkflowInstanceCount()).isZero();
    }
}