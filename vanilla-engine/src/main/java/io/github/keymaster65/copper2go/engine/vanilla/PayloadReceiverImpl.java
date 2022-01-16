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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;

public class PayloadReceiverImpl implements PayloadReceiver {
    private final VanillaEngineImpl vanillaEngineImpl;

    public PayloadReceiverImpl(final VanillaEngineImpl vanillaEngineImpl) {
        this.vanillaEngineImpl = vanillaEngineImpl;
    }

    @Override
    public void receive(final String payload, final Map<String, String> attributes, final ReplyChannel replyChannel, final String workflow, final long major, final long minor) throws EngineException {
        Workflow processInstance;
        try {
            processInstance = createWorkflowInstance(workflow, major, minor);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new EngineException("Can't create workflow instance.", e);
        }
        WorkflowData workflowData = storeReplyChannel(payload, attributes, replyChannel);
        vanillaEngineImpl.executorService.submit(() ->
                processInstance.main(workflowData)
        );
    }


    private WorkflowData storeReplyChannel(final String payload, final Map<String, String> attributes, final ReplyChannel replyChannel) {
        String uuid = null;
        if (replyChannel != null) {
            uuid = UUID.randomUUID().toString();
            vanillaEngineImpl.replyChannelStore.store(uuid, replyChannel);
        }
        return new WorkflowData(uuid, payload, attributes);
    }

    Workflow createWorkflowInstance(final String workflow, final long major, final long minor) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final Class<?> clazz = Class.forName("io.github.keymaster65.copper2go.engine.vanilla.workflow." + workflow + "_" + major + "_" + minor);
        final Constructor<?> constructor = clazz.getConstructor(VanillaEngine.class);
        return (Workflow) constructor.newInstance(
                vanillaEngineImpl
        );
    }
}
