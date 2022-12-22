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
package io.github.keymaster65.copper2go.sync.application.workflow;

import io.github.keymaster65.copper2go.engine.sync.engineapi.SyncEngine;
import io.github.keymaster65.copper2go.engine.sync.workflowapi.Workflow;
import io.github.keymaster65.copper2go.engine.sync.workflowapi.WorkflowFactory;

public class WorkflowFactoryImpl implements WorkflowFactory {
    private final SyncEngine syncEngine;

    public WorkflowFactoryImpl(final SyncEngine syncEngine) {
        this.syncEngine = syncEngine;
    }

    @Override
    public Workflow create(final String workflow, final long major, final long minor) {

        final String versionedWorkflow = "%s.%d.%d".formatted(workflow, major, minor);
        return switch (versionedWorkflow) {
            case "Hello.2.0" -> new Hello2(syncEngine);
            case "Pricing.1.0" -> new Pricing1(syncEngine);
            default -> throw new IllegalArgumentException("Unknown workflow %s.".formatted(versionedWorkflow));
        };
    }
}
