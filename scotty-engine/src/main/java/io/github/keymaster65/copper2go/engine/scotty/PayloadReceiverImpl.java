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
package io.github.keymaster65.copper2go.engine.scotty;

import io.github.keymaster65.copper2go.engine.ReplyChannelStoreImpl;
import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.api.connector.PayloadReceiver;
import io.github.keymaster65.copper2go.api.connector.ReplyChannel;
import io.github.keymaster65.copper2go.api.workflow.WorkflowData;
import org.copperengine.core.CopperException;
import org.copperengine.core.WorkflowInstanceDescr;
import org.copperengine.core.WorkflowVersion;
import org.copperengine.core.tranzient.TransientScottyEngine;

import java.util.Map;

public class PayloadReceiverImpl implements PayloadReceiver {

    private final TransientScottyEngine scottyEngine;
    private final ReplyChannelStoreImpl replyChannelStore;

    PayloadReceiverImpl(
            final TransientScottyEngine scottyEngine,
            final ReplyChannelStoreImpl replyChannelStore
    ) {

        this.scottyEngine = scottyEngine;
        this.replyChannelStore = replyChannelStore;
    }

    @Override
    public void receive(
            final String payload,
            final Map<String, String> attributes,
            final ReplyChannel replyChannel,
            final String workflow,
            final long major,
            final long minor
    ) throws EngineException {
        WorkflowInstanceDescr<WorkflowData> workflowInstanceDescr = new WorkflowInstanceDescr<>(workflow);
        WorkflowVersion version = scottyEngine.getWfRepository().findLatestMinorVersion(workflowInstanceDescr.getWfName(), major, minor);
        workflowInstanceDescr.setVersion(version);

        String uuid = null;
        if (replyChannel != null) {
            uuid = scottyEngine.createUUID();
            replyChannelStore.store(uuid, replyChannel);
        }
        workflowInstanceDescr.setData(new WorkflowData(uuid, payload, attributes));
        try {
            scottyEngine.run(workflowInstanceDescr);
        } catch (CopperException e) {
            throw new EngineException("Exception while running workflow.", e);
        }
    }
}
