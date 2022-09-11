package io.github.keymaster65.copper2go.engine.vanilla;

public interface WorkflowFactory {
    Workflow of(
            final String workflow,
            final long major,
            final long minor
    );
}
