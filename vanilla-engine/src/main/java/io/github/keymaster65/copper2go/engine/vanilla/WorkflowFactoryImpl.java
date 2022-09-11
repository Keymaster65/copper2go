package io.github.keymaster65.copper2go.engine.vanilla;

import io.github.keymaster65.copper2go.engine.vanilla.workflow.Hello2;
import io.github.keymaster65.copper2go.engine.vanilla.workflow.Pricing1;

public class WorkflowFactoryImpl implements WorkflowFactory {

    private final VanillaEngine vanillaEngine;

    public WorkflowFactoryImpl (final VanillaEngine vanillaEngine) {
        this.vanillaEngine = vanillaEngine;
    }

    @Override
    public Workflow of(final String workflow, final long major, final long minor) {
        final String versionedWorkflow = "%s.%d.%d".formatted(workflow, major, minor);
        return switch (versionedWorkflow) {
            case "Hello.2.0" -> new Hello2(vanillaEngine);
            case "Pricing.1.0" -> new Pricing1(vanillaEngine);
            default -> throw new IllegalArgumentException("Unknown workflow %s.".formatted(versionedWorkflow));
        };
    }
}
