package io.github.keymaster65.copper2go.engine.vanilla;

import io.github.keymaster65.copper2go.engine.vanilla.workflow.Hello2;
import io.github.keymaster65.copper2go.engine.vanilla.workflow.Pricing1;
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