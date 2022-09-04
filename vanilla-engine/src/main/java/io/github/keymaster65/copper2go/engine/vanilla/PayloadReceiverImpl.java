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
package io.github.keymaster65.copper2go.engine.vanilla;

import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.api.connector.PayloadReceiver;
import io.github.keymaster65.copper2go.api.connector.ReplyChannel;
import io.github.keymaster65.copper2go.api.workflow.WorkflowData;
import io.github.keymaster65.copper2go.engine.vanilla.workflow.Hello_2_0;
import io.github.keymaster65.copper2go.engine.vanilla.workflow.Pricing_1_0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

public class PayloadReceiverImpl implements PayloadReceiver {

    private static final Logger log = LoggerFactory.getLogger(PayloadReceiverImpl.class);

    private final VanillaEngineImpl vanillaEngineImpl;
    private final WorkflowInstanceHolder workflowInstanceHolder;

    public PayloadReceiverImpl(final VanillaEngineImpl vanillaEngineImpl, final WorkflowInstanceHolder workflowInstanceHolder) {
        this.vanillaEngineImpl = vanillaEngineImpl;
        this.workflowInstanceHolder = workflowInstanceHolder;
    }

    @Override
    public void receive(final String payload, final Map<String, String> attributes, final ReplyChannel replyChannel, final String workflow, final long major, final long minor) throws EngineException {
        Workflow workflowInstance;
        try {
            workflowInstance = createWorkflowInstance(workflow, major, minor);
        } catch (RuntimeException e) {
            throw new EngineException("Can't create workflow instance.", e);
        }

        WorkflowData workflowData = storeReplyChannel(payload, attributes, replyChannel);
        final Future<?> workflowInstanceFuture = vanillaEngineImpl.executorService
                .submit(() -> workflowInstance.main(workflowData));
        handleWorkflowInstanceResult(workflowInstanceFuture, workflowInstance);
    }

    private void handleWorkflowInstanceResult(final Future<?> workflowInstanceFuture, final Workflow workflowInstance) {
        workflowInstanceHolder.add(workflowInstanceFuture, workflowInstance);
    }


    // TODO split this method
    private WorkflowData storeReplyChannel(final String payload, final Map<String, String> attributes, final ReplyChannel replyChannel) {
        String uuid = null;
        if (replyChannel != null) {
            uuid = UUID.randomUUID().toString();
            log.debug("Store replyChannel with uuid {}: {}", uuid, replyChannel);
            vanillaEngineImpl.replyChannelStore.store(uuid, replyChannel);
        } else {
            log.debug("Ignore empty replyChannel.");
        }
        return new WorkflowData(uuid, payload, attributes);
    }

    // TODO move this to a factory interface
    Workflow createWorkflowInstance(final String workflow, final long major, final long minor) {
        final String versionedWorkflow = "%s.%d.%d".formatted(workflow, major, minor);
        switch (versionedWorkflow) {
            case "Hello.2.0":
                return new Hello_2_0(vanillaEngineImpl);
            case "Pricing.1.0":
                return new Pricing_1_0(vanillaEngineImpl);
            default:
                throw new IllegalArgumentException("Unknown workflow %s.".formatted(versionedWorkflow));
        }
    }
}
