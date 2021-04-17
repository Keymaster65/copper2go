package de.wolfsvl.copper2go.util;

import de.wolfsvl.copper2go.workflowapi.WorkflowData;
import org.copperengine.core.CopperException;
import org.copperengine.core.DependencyInjector;
import org.copperengine.core.EngineState;
import org.copperengine.core.WorkflowInstanceDescr;
import org.copperengine.core.WorkflowVersion;
import org.copperengine.core.tranzient.TransientEngineFactory;
import org.copperengine.core.tranzient.TransientScottyEngine;

import java.io.File;
import java.util.concurrent.locks.LockSupport;

import static org.assertj.core.api.Assertions.assertThat;

public final class WorkflowTestRunner {

    private WorkflowTestRunner() {}

    public static class WorkflowDefinition {
        public final String workflowName;
        public final long majorVersion;
        public final long minorVersion;

        public WorkflowDefinition(
                final String workflowName,
                final long majorVersion,
                final long minorVersion
        ) {
            this.workflowName = workflowName;
            this.majorVersion = majorVersion;
            this.minorVersion = minorVersion;
        }
    }

    public static void runTest(
            final WorkflowData workflowData,
            final WorkflowDefinition workflowDefinition,
            final TransientScottyEngine engine
    ) throws CopperException {
        try {
            engine.getState();
            assertThat(engine.getState()).isEqualTo(EngineState.STARTED.toString()); // NOSONAR

            WorkflowVersion version = engine.getWfRepository().findLatestMinorVersion(workflowDefinition.workflowName, workflowDefinition.majorVersion, workflowDefinition.minorVersion);
            WorkflowInstanceDescr<WorkflowData> workflowInstanceDescr = new WorkflowInstanceDescr<>(workflowDefinition.workflowName, workflowData, null, null, null, version);

            engine.run(workflowInstanceDescr);
            while (engine.getNumberOfWorkflowInstances() > 0) {
                LockSupport.parkNanos(100000000L);
            }
        } finally {
            engine.shutdown();
        }
    }

    public static TransientScottyEngine createTestEngine(final String workflowDir, Copper2goDependencyInjector copper2goDependencyInjector) {
        TransientEngineFactory factory = createTransientEngineFactory(workflowDir, copper2goDependencyInjector);
        return factory.create();
    }

    private static TransientEngineFactory createTransientEngineFactory(final String workflowDir, final Copper2goDependencyInjector copper2goDependencyInjector) {
        return new TransientEngineFactory() {
            @Override
            protected File getWorkflowSourceDirectory() {
                return new File(workflowDir);
            }

            @Override
            protected DependencyInjector createDependencyInjector() {
                return copper2goDependencyInjector;
            }
        };
    }
}

