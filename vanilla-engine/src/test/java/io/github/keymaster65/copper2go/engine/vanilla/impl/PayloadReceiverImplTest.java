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
import io.github.keymaster65.copper2go.api.connector.ReplyChannel;
import io.github.keymaster65.copper2go.api.workflow.EventChannelStore;
import io.github.keymaster65.copper2go.api.workflow.RequestChannelStore;
import io.github.keymaster65.copper2go.api.workflow.WorkflowData;
import io.github.keymaster65.copper2go.engine.ReplyChannelStoreImpl;
import io.github.keymaster65.copper2go.engine.vanilla.workflowapi.Workflow;
import io.github.keymaster65.copper2go.engine.vanilla.workflowapi.WorkflowFactory;
import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;

import java.util.Map;
import java.util.concurrent.ExecutorService;

class PayloadReceiverImplTest {

    public static final String PAYLOAD = "payload";

    @Example
    void receive() throws EngineException {
        final ExecutorService executorService = Mockito.mock(ExecutorService.class);
        @SuppressWarnings("unchecked") final FutureStore<Continuation> continuationStore = Mockito.mock(FutureStore.class);
        final ExpectedResponsesStore expectedResponsesStore = Mockito.mock(ExpectedResponsesStore.class);
        final VanillaEngineImpl engine = new VanillaEngineImpl(
                Mockito.mock(ReplyChannelStoreImpl.class),
                Mockito.mock(RequestChannelStore.class),
                Mockito.mock(EventChannelStore.class),
                executorService,
                continuationStore,
                expectedResponsesStore
        );
        @SuppressWarnings("unchecked") final FutureStore<Workflow> workflowStore = Mockito.mock(FutureStore.class);
        final PayloadReceiverImpl payloadReceiver = new PayloadReceiverImpl(
                engine,
                workflowStore,
                Mockito.mock(WorkflowFactory.class)
        );

        payloadReceiver.receive(
                PAYLOAD,
                Map.of(),
                Mockito.mock(ReplyChannel.class),
                "Any",
                0,
                0
        );

        Mockito.verify(workflowStore).addFuture(Mockito.any(), Mockito.any());
        Mockito.verify(executorService).submit((Runnable) Mockito.any());
    }


    @Example
    void receiveForUnknownWorkflow() {
        final VanillaEngineImpl engine = Mockito.mock(VanillaEngineImpl.class);
        @SuppressWarnings("unchecked") final FutureStore<Workflow> mock = Mockito.mock(FutureStore.class);
        final WorkflowFactory workflowFactory = Mockito.mock(WorkflowFactory.class);
        Mockito
                .when(workflowFactory.of("Unknown", 0L, 0L))
                .thenThrow(new IllegalArgumentException("Test"));
        final PayloadReceiverImpl payloadReceiver = new PayloadReceiverImpl(
                engine,
                mock,
                workflowFactory
        );

        Assertions.assertThatCode(() ->
                        payloadReceiver.receive(
                                PAYLOAD,
                                Map.of(),
                                Mockito.mock(ReplyChannel.class),
                                "Unknown",
                                0,
                                0
                        )
                )
                .isInstanceOf(EngineException.class);
    }

    @Example
    void createAndStoreNotNullReplyChannel() {
        final ReplyChannelStoreImpl replyChannelStore = Mockito.mock(ReplyChannelStoreImpl.class);
        @SuppressWarnings("unchecked") final FutureStore<Continuation> continuationStore = Mockito.mock(FutureStore.class);

        final VanillaEngineImpl engine = new VanillaEngineImpl(
                replyChannelStore,
                Mockito.mock(RequestChannelStore.class),
                Mockito.mock(EventChannelStore.class),
                Mockito.mock(ExecutorService.class),
                continuationStore,
                Mockito.mock(ExpectedResponsesStore.class)
        );
        @SuppressWarnings("unchecked") final FutureStore<Workflow> workflowStore = Mockito.mock(FutureStore.class);
        final PayloadReceiverImpl payloadReceiver = new PayloadReceiverImpl(
                engine,
                workflowStore,
                Mockito.mock(WorkflowFactory.class)
        );
        final ReplyChannel replyChannel = Mockito.mock(ReplyChannel.class);
        final Map<String, String> attributes = Map.of();

        final WorkflowData workflowData = payloadReceiver.createAndStoreNotNullReplyChannel(PAYLOAD, attributes, replyChannel);

        Assertions.assertThat(workflowData.getUUID()).isNotEmpty();
        Assertions.assertThat(workflowData.getPayload()).isEqualTo(PAYLOAD);
        Assertions.assertThat(workflowData.getAttributes()).isEqualTo(attributes);
        Mockito.verify(replyChannelStore).store(Mockito.anyString(), Mockito.eq(replyChannel));
        Mockito.verifyNoMoreInteractions(replyChannelStore);
    }

    @Example
    void createAndStoreNotNullReplyChannelNull() {
        final ReplyChannelStoreImpl replyChannelStore = Mockito.mock(ReplyChannelStoreImpl.class);
        @SuppressWarnings("unchecked") final FutureStore<Continuation> continuationStore = Mockito.mock(FutureStore.class);
        final VanillaEngineImpl engine = new VanillaEngineImpl(
                replyChannelStore,
                Mockito.mock(RequestChannelStore.class),
                Mockito.mock(EventChannelStore.class),
                Mockito.mock(ExecutorService.class),
                continuationStore,
                Mockito.mock(ExpectedResponsesStore.class)
        );
        @SuppressWarnings("unchecked") final FutureStore<Workflow> workflowStore = Mockito.mock(FutureStore.class);
        final PayloadReceiverImpl payloadReceiver = new PayloadReceiverImpl(
                engine,
                workflowStore,
                Mockito.mock(WorkflowFactory.class)
        );
        final Map<String, String> attributes = Map.of();

        final WorkflowData workflowData = payloadReceiver.createAndStoreNotNullReplyChannel(PAYLOAD, Map.of(), null);

        Assertions.assertThat(workflowData.getUUID()).isNull();
        Assertions.assertThat(workflowData.getPayload()).isEqualTo(PAYLOAD);
        Assertions.assertThat(workflowData.getAttributes()).isEqualTo(attributes);
        Mockito.verifyNoInteractions(replyChannelStore);
    }
}