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

import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.api.connector.PayloadReceiver;
import io.github.keymaster65.copper2go.api.connector.ReplyChannel;
import io.github.keymaster65.copper2go.api.workflow.WorkflowData;
import io.github.keymaster65.copper2go.engine.vanilla.workflowapi.Workflow;
import io.github.keymaster65.copper2go.engine.vanilla.workflowapi.WorkflowFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

class PayloadReceiverImpl implements PayloadReceiver {

    private static final Logger log = LoggerFactory.getLogger(PayloadReceiverImpl.class);

    private final VanillaEngineImpl vanillaEngineImpl;
    private final FutureStore<Workflow> workflowStore;
    private final WorkflowFactory workflowFactory;

    public PayloadReceiverImpl(
            final VanillaEngineImpl vanillaEngineImpl,
            final FutureStore<Workflow> workflowStore,
            final WorkflowFactory workflowFactory
    ) {
        this.vanillaEngineImpl = vanillaEngineImpl;
        this.workflowStore = workflowStore;
        this.workflowFactory = workflowFactory;
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
        Workflow workflowInstance;
        try {
            workflowInstance = workflowFactory.of(workflow, major, minor);
        } catch (RuntimeException e) {
            throw new EngineException("Can't create workflow instance.", e);
        }

        WorkflowData workflowData = createAndStoreNotNullReplyChannel(payload, attributes, replyChannel);
        final Future<?> workflowInstanceFuture = vanillaEngineImpl.executorService
                .submit(() -> workflowInstance.main(workflowData));
        storeWorkflowInstance(workflowInstanceFuture, workflowInstance);
    }
    private void storeWorkflowInstance(final Future<?> workflowInstanceFuture, final Workflow workflowInstance) {
        workflowStore.addFuture(workflowInstanceFuture, workflowInstance);
    }

    WorkflowData createAndStoreNotNullReplyChannel(final String payload, final Map<String, String> attributes, final ReplyChannel replyChannel) {
        String uuid = null;
        if (replyChannel != null) {
            uuid = UUID.randomUUID().toString();
            log.debug("Store replyChannel with uuid {}: {}", uuid, replyChannel);
            vanillaEngineImpl.replyChannelStore.store(uuid, replyChannel);
        } else {
            log.debug("Ignore empty replyChannel. Seems to be a aoneway call.");
        }
        return new WorkflowData(uuid, payload, attributes);
    }
}
