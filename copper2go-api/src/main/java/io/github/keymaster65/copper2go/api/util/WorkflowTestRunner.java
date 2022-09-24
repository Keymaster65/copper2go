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
package io.github.keymaster65.copper2go.api.util;

import io.github.keymaster65.copper2go.api.workflow.WorkflowData;
import org.copperengine.core.CopperException;
import org.copperengine.core.DependencyInjector;
import org.copperengine.core.EngineState;
import org.copperengine.core.WorkflowInstanceDescr;
import org.copperengine.core.WorkflowVersion;
import org.copperengine.core.tranzient.TransientEngineFactory;
import org.copperengine.core.tranzient.TransientScottyEngine;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.locks.LockSupport;

/**
 * Utility the helps to create tested copper2go workflow.
 */
public final class WorkflowTestRunner {

    private WorkflowTestRunner() {
    }

    /**
     * Container for attributes, that defines a workflow definition.
     */
    public static class WorkflowDefinition {

        /**
         * Name of the workflow.
         */
        public final String workflowName;

        /**
         * Major version of the workflow.
         */
        public final long majorVersion;


        /**
         * Minor version of the workflow.
         */
        public final long minorVersion;

        /**
         * Creates a definition with given attributes.
         *
         * @param workflowName of the workflow
         * @param majorVersion of the workflow
         * @param minorVersion of the workflow
         */
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

    /**
     * Run the test:
     * <ol>
     *     <li>creates and runs a workflow instance</li>
     *     <li>waits for all instances to finish</li>
     *     <li>shuts the engine down</li>
     * </ol>
     *
     * @param workflowData       input for workflow instance
     * @param workflowDefinition the tested worklow
     * @param engine             started engine
     * @throws CopperException is engine is not started or exception to run the instance
     */
    public static void runTest(
            final WorkflowData workflowData,
            final WorkflowDefinition workflowDefinition,
            final TransientScottyEngine engine
    ) throws CopperException {
        try {
            String state = engine.getState();
            if (!EngineState.STARTED.toString().equals(state)) {
                throw new CopperException(String.format("Engine not started. State =%s", state));
            }

            WorkflowVersion version = engine.getWfRepository().findLatestMinorVersion(workflowDefinition.workflowName, workflowDefinition.majorVersion, workflowDefinition.minorVersion);
            WorkflowInstanceDescr<WorkflowData> workflowInstanceDescr = new WorkflowInstanceDescr<>(workflowDefinition.workflowName, workflowData, null, null, null, version);

            engine.run(workflowInstanceDescr);
            while (engine.getNumberOfWorkflowInstances() > 0) {
                LockSupport.parkNanos(Duration.ofMillis(100).toNanos());
            }
        } finally {
            engine.shutdown();
        }
    }

    /**
     * Creates the copper engine for a test.
     *
     * @param workflowDir                 directory that contains the workflows
     * @param copper2goDependencyInjector uses as injector in test
     * @return the copper test engine
     */
    public static TransientScottyEngine createTestEngine(final String workflowDir, Copper2goDependencyInjector copper2goDependencyInjector) {
        var factory = createTransientEngineFactory(workflowDir, copper2goDependencyInjector);
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

