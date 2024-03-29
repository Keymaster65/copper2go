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


import io.github.keymaster65.copper2go.api.workflow.EventChannelStore;
import io.github.keymaster65.copper2go.api.workflow.RequestChannelStore;
import io.github.keymaster65.copper2go.engine.ReplyChannelStoreImpl;
import net.jqwik.api.Example;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

class VanillaEngineImplTest {

    public static final String UUID = "uuid";
    public static final String REPLY = "reply";
    public static final String CHANNEL_NAME = "channelName";
    public static final String REQUEST = "request";
    public static final String CORRELATION_ID = "correlationid";
    public static final String RESPONSE = "response";


    @Example
    void event() {
        final EventChannelStore eventChannelStore = Mockito.mock(EventChannelStore.class);
        @SuppressWarnings("unchecked") final FutureStore<Continuation> continuationStore = Mockito.mock(FutureStore.class);

        final VanillaEngineImpl engine = new VanillaEngineImpl(
                Mockito.mock(ReplyChannelStoreImpl.class),
                Mockito.mock(RequestChannelStore.class),
                eventChannelStore,
                Mockito.mock(ExecutorService.class),
                continuationStore,
                Mockito.mock(ExpectedResponsesStore.class)
        );

        engine.event(UUID, REPLY);

        Mockito.verify(eventChannelStore).event(UUID, REPLY);
    }

    @Example
    void request() {
        final RequestChannelStore requestChannelStore = Mockito.mock(RequestChannelStore.class);
        @SuppressWarnings("unchecked") final FutureStore<Continuation> continuationStore = Mockito.mock(FutureStore.class);

        final VanillaEngineImpl engine = new VanillaEngineImpl(
                Mockito.mock(ReplyChannelStoreImpl.class),
                requestChannelStore,
                Mockito.mock(EventChannelStore.class),
                Mockito.mock(ExecutorService.class),
                continuationStore,
                Mockito.mock(ExpectedResponsesStore.class)
        );

        engine.request(CHANNEL_NAME, REQUEST);

        Mockito.verify(requestChannelStore).request(Mockito.eq(CHANNEL_NAME), Mockito.eq(REQUEST), Mockito.anyString());
    }

    @Example
    void reply() {
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

        engine.reply(UUID, REPLY);

        Mockito.verify(replyChannelStore).reply(UUID, REPLY);
    }

    @Example
    void continueAsync() {
        final ExpectedResponsesStore expectedResponsesStore = Mockito.mock(ExpectedResponsesStore.class);
        @SuppressWarnings("unchecked") final FutureStore<Continuation> continuationStore = Mockito.mock(FutureStore.class);
        final VanillaEngineImpl engine = new VanillaEngineImpl(
                Mockito.mock(ReplyChannelStoreImpl.class),
                Mockito.mock(RequestChannelStore.class),
                Mockito.mock(EventChannelStore.class),
                Mockito.mock(ExecutorService.class),
                continuationStore,
                expectedResponsesStore
        );
        @SuppressWarnings("unchecked") final Consumer<String> consumer = Mockito.mock(Consumer.class);

        engine.continueAsync(CORRELATION_ID, consumer);

        Mockito.verify(expectedResponsesStore).addExpectedResponse(CORRELATION_ID, new Continuation(consumer));
    }

    @Example
    void continueAsyncEarlyResponse() {
        @SuppressWarnings("unchecked") final FutureStore<Continuation> continuationStore = Mockito.mock(FutureStore.class);
        final ExpectedResponsesStore expectedResponsesStore = Mockito.mock(ExpectedResponsesStore.class);
        final ExecutorService executorService = Mockito.mock(ExecutorService.class);
        final VanillaEngineImpl engine = new VanillaEngineImpl(
                Mockito.mock(ReplyChannelStoreImpl.class),
                Mockito.mock(RequestChannelStore.class),
                Mockito.mock(EventChannelStore.class),
                executorService,
                continuationStore,
                expectedResponsesStore
        );
        @SuppressWarnings("unchecked") final Consumer<String> consumer = Mockito.mock(Consumer.class);
        final Continuation continuation = new Continuation(consumer);
        final Continuation earlyResponseContinuation = new Continuation(RESPONSE);
        Mockito
                .when(expectedResponsesStore.addExpectedResponse(CORRELATION_ID, continuation))
                .thenReturn(earlyResponseContinuation);

        engine.continueAsync(CORRELATION_ID, consumer);

        Mockito.verify(expectedResponsesStore).addExpectedResponse(CORRELATION_ID, continuation);
        Mockito.verify(expectedResponsesStore).removeExpectedResponse(CORRELATION_ID);
        Mockito.verify(executorService).submit((Runnable) Mockito.any());
    }


}