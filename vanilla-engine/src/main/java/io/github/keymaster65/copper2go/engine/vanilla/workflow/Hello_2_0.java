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
package io.github.keymaster65.copper2go.engine.vanilla.workflow;

import io.github.keymaster65.copper2go.api.workflow.WorkflowData;
import io.github.keymaster65.copper2go.engine.vanilla.VanillaEngine;
import io.github.keymaster65.copper2go.engine.vanilla.Workflow;

import java.util.function.Consumer;

public class Hello_2_0 implements Workflow {

    private final VanillaEngine vanillaEngine;
    private WorkflowData workflowData;

    public Hello_2_0(
            final VanillaEngine vanillaEngine
    ) {
        this.vanillaEngine = vanillaEngine;
    }

    @Override
    public void main(final WorkflowData workflowData) {
        vanillaEngine.event("System.stdout", "workflow " + Hello_2_0.class.getSimpleName());
        Consumer<String> continuation = this::continuation;
        this.workflowData = workflowData;
        final String responseCorrelationId = vanillaEngine.request("Pricing.centPerMinute", "request");
        vanillaEngine.continueAsync(responseCorrelationId, continuation);
    }

    public void continuation(final String response) {
        final String uuid = workflowData.getUUID();
        if (uuid != null) {
            vanillaEngine.reply(uuid, "Hello " + workflowData.getPayload() + "! Please transfer " + response);
        }
    }
}
