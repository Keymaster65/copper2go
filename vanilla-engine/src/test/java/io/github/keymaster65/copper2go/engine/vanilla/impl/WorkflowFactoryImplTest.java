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

import io.github.keymaster65.copper2go.engine.vanilla.workflow.Hello2;
import io.github.keymaster65.copper2go.engine.vanilla.workflow.Pricing1;
import io.github.keymaster65.copper2go.engine.vanilla.workflow.WorkflowFactoryImpl;
import io.github.keymaster65.copper2go.engine.vanilla.workflowapi.Workflow;
import io.github.keymaster65.copper2go.engine.vanilla.workflowapi.WorkflowFactory;
import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;

class WorkflowFactoryImplTest {
    final WorkflowFactory workflowFactory = new WorkflowFactoryImpl(Mockito.mock(VanillaEngineImpl.class));

    @Example
    void createWorkflowInstanceHello2() {
        final Workflow workflowInstance = workflowFactory.of("Hello", 2, 0);

        Assertions.assertThat(workflowInstance).isInstanceOf(Hello2.class);
    }

    @Example
    void createWorkflowInstancePricing1() {
        final Workflow workflowInstance = workflowFactory.of("Pricing", 1, 0);

        Assertions.assertThat(workflowInstance).isInstanceOf(Pricing1.class);
    }

    @Example
    void createWorkflowInstanceUndefined() {
        Assertions.assertThatCode(
                () -> workflowFactory.of("Unknown", 0, 0)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}