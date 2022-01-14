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

import io.github.keymaster65.copper2go.api.workflow.EventChannelStore;
import io.github.keymaster65.copper2go.api.workflow.ReplyChannelStore;
import io.github.keymaster65.copper2go.api.workflow.RequestChannelStore;
import io.github.keymaster65.copper2go.api.workflow.WorkflowData;
import io.github.keymaster65.copper2go.engine.vanilla.VanillaEngine;
import io.github.keymaster65.copper2go.engine.vanilla.Workflow;

import java.util.function.Consumer;

public class Hello_2_0 implements Workflow {

    private final VanillaEngine vanillaEngine;
    private final ReplyChannelStore replyChannelStore;
    private final RequestChannelStore requestChannelStore;
    private WorkflowData workflowData;

    public Hello_2_0(
            final VanillaEngine vanillaEngine,
            final ReplyChannelStore replyChannelStore,
            final RequestChannelStore requestChannelStore,
            @SuppressWarnings("unused") final EventChannelStore eventChannelStore
    ) {
        this.vanillaEngine = vanillaEngine;
        this.replyChannelStore = replyChannelStore;
        this.requestChannelStore = requestChannelStore;
    }

    public void main(final WorkflowData workflowData) {
        Consumer<String> continuation = this::continuation;
        this.workflowData = workflowData;
        final String responseCorrelationId = vanillaEngine.createUUID();
        requestChannelStore.request("Pricing.centPerMinute", "request", responseCorrelationId);
        vanillaEngine.continueAsSync(responseCorrelationId, continuation);
    }

    public void continuation(final String response) {
        final String uuid = workflowData.getUUID();
        if (uuid != null) {
            replyChannelStore.reply(uuid, "Hello " + workflowData.getPayload() + " Response: " + response);
        }
    }
}
