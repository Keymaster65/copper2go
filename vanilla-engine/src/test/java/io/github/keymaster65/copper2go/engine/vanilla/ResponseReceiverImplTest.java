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

import io.github.keymaster65.copper2go.api.workflow.EventChannelStore;
import io.github.keymaster65.copper2go.api.workflow.RequestChannelStore;
import io.github.keymaster65.copper2go.engine.ReplyChannelStoreImpl;
import net.jqwik.api.Example;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

class ResponseReceiverImplTest {

    public static final String CORRELATION_ID = "correlationId";
    public static final String RESPONSE = "response";

    private static final Logger log = LoggerFactory.getLogger(ResponseReceiverImplTest.class);

    @Example
    void receiveEarlyResponse() {
        final ExecutorService executorService = Mockito.mock(ExecutorService.class);
        final ContinuationStore continuationStore = Mockito.mock(ContinuationStore.class);
        final VanillaEngineImpl engine = new VanillaEngineImpl(
                Mockito.mock(ReplyChannelStoreImpl.class),
                Mockito.mock(RequestChannelStore.class),
                Mockito.mock(EventChannelStore.class),
                executorService,
                continuationStore
        );
        final ResponseReceiverImpl responseReceiver = new ResponseReceiverImpl(engine);

        responseReceiver.receiveError(CORRELATION_ID, RESPONSE);

        // sorry for that hack ;-)
        Mockito.verify(continuationStore).addExpectedResponse(CORRELATION_ID, new Continuation(RESPONSE));
        responseReceiver.receive(CORRELATION_ID, RESPONSE);

        Mockito.verify(continuationStore, Mockito.times(2)).addExpectedResponse(CORRELATION_ID, new Continuation(RESPONSE));
        Mockito.verifyNoMoreInteractions(continuationStore);
        Mockito.verifyNoInteractions(executorService);
    }

    @Example
    void receive() {
        final ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        final ContinuationStore continuationStore = Mockito.mock(ContinuationStore.class);
        @SuppressWarnings("unchecked") final Consumer<String> consumer = Mockito.mock(Consumer.class);
        final Continuation waiting = new Continuation(consumer);
        Mockito
                .when(continuationStore.addExpectedResponse(Mockito.eq(CORRELATION_ID), Mockito.any()))
                .thenReturn(waiting);
        final VanillaEngineImpl engine = new VanillaEngineImpl(
                Mockito.mock(ReplyChannelStoreImpl.class),
                Mockito.mock(RequestChannelStore.class),
                Mockito.mock(EventChannelStore.class),
                executorService,
                continuationStore
        );
        final ResponseReceiverImpl responseReceiver = new ResponseReceiverImpl(engine);


        responseReceiver.receive(CORRELATION_ID, RESPONSE);
        executorService.shutdown();
        try {
            final boolean ignored = executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Ignore InterruptedException.");
            final boolean ignored = Thread.currentThread().isInterrupted();
        }

        Mockito.verify(consumer).accept(RESPONSE);
        Mockito.verify(continuationStore).addExpectedResponse(CORRELATION_ID, new Continuation(RESPONSE));
        Mockito.verify(continuationStore).removeExpectedResponse(CORRELATION_ID);
        Mockito.verify(continuationStore).addFuture(Mockito.any(), Mockito.eq(waiting));
        Mockito.verifyNoMoreInteractions(continuationStore);
    }
}