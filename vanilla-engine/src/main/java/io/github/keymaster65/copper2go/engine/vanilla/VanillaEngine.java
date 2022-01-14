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
import io.github.keymaster65.copper2go.api.connector.ResponseReceiver;
import io.github.keymaster65.copper2go.api.workflow.EventChannelStore;
import io.github.keymaster65.copper2go.api.workflow.ReplyChannelStore;
import io.github.keymaster65.copper2go.api.workflow.RequestChannelStore;
import io.github.keymaster65.copper2go.api.workflow.WorkflowData;
import io.github.keymaster65.copper2go.engine.ReplyChannelStoreImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class VanillaEngine implements PayloadReceiver, ResponseReceiver {
    private final ReplyChannelStoreImpl replyChannelStore;
    private final RequestChannelStore requestChannelStore;
    private final EventChannelStore eventChannelStore;
    private final ExecutorService executorService;
    private final Map<String, Continuation> expectedResponses = new ConcurrentHashMap<>();

    private record Continuation(
            Consumer<String> consumer,
            String response
    ) {

        private Continuation(String response) {
            this(null, response);
        }

        private Continuation(Consumer<String> consumer
        ) {
            this(consumer, null);
        }
    }

    public VanillaEngine(
            final ReplyChannelStoreImpl replyChannelStore,
            final RequestChannelStore requestChannelStore,
            final EventChannelStore eventChannelStore,
            final ExecutorService executorService
    ) {
        this.replyChannelStore = replyChannelStore;
        this.requestChannelStore = requestChannelStore;
        this.eventChannelStore = eventChannelStore;
        this.executorService = executorService;
    }

    @Override
    public void receive(final String payload, final Map<String, String> attributes, final ReplyChannel replyChannel, final String workflow, final long major, final long minor) throws EngineException {
        Workflow workflowInstance;
        try {
            workflowInstance = createWorkflowInstance(workflow, major, minor);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new EngineException("Can't create workflow instance.", e);
        }
        WorkflowData workflowData = createWorkflowData(payload, attributes, replyChannel);
        executorService.submit(() ->
                workflowInstance.main(workflowData)
        );
    }

    public void continueAsSync(final String responseCorrelationId, final Consumer<String> consumer) {
        Continuation earlyResponse = expectedResponses.put(responseCorrelationId, new Continuation(consumer));
        if (earlyResponse != null) {
            expectedResponses.remove(responseCorrelationId);
            executorService.submit(() ->
                    consumer.accept(earlyResponse.response)
            );
        }
    }

    @Override
    public void receive(final String responseCorrelationId, final String response) {
        Continuation waiting = expectedResponses.put(responseCorrelationId, new Continuation(response));
        if (waiting != null) {
            expectedResponses.remove(responseCorrelationId);
            executorService.submit(() ->
                    waiting.consumer().accept(response)
            );
        }
    }

    @Override
    public void receiveError(final String responseCorrelationId, final String response) {
        receive(responseCorrelationId, response);
    }

    public String createUUID() {
        return UUID.randomUUID().toString();
    }

    private WorkflowData createWorkflowData(final String payload, final Map<String, String> attributes, final ReplyChannel replyChannel) {
        String uuid = null;
        if (replyChannel != null) {
            uuid = createUUID();
            replyChannelStore.store(uuid, replyChannel);
        }
        return new WorkflowData(uuid, payload, attributes);
    }

    Workflow createWorkflowInstance(final String workflow, final long major, final long minor) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final Class<?> clazz = Class.forName("io.github.keymaster65.copper2go.engine.vanilla.workflow." + workflow + "_" + major + "_" + minor);
        final Constructor<?> constructor = clazz.getConstructor(VanillaEngine.class, ReplyChannelStore.class, RequestChannelStore.class, EventChannelStore.class);
        return (Workflow) constructor.newInstance(
                this,
                replyChannelStore,
                requestChannelStore,
                eventChannelStore
        );
    }
}
