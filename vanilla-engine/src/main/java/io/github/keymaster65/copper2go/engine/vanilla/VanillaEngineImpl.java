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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class VanillaEngineImpl implements VanillaEngine {

    final ReplyChannelStoreImpl replyChannelStore;
    final RequestChannelStore requestChannelStore;
    final EventChannelStore eventChannelStore;
    final ExecutorService executorService;
    final FutureStore<Continuation> continuationStore;
    final ExpectedResponsesStore expectedResponsesStore;
    private static final Logger log = LoggerFactory.getLogger(VanillaEngineImpl.class);

    public VanillaEngineImpl(
            final ReplyChannelStoreImpl replyChannelStore,
            final RequestChannelStore requestChannelStore,
            final EventChannelStore eventChannelStore,
            final ExecutorService executorService,
            final FutureStore<Continuation>  continuationStore,
            final ExpectedResponsesStore expectedResponsesStore
    ) {
        this.replyChannelStore = replyChannelStore;
        this.requestChannelStore = requestChannelStore;
        this.eventChannelStore = eventChannelStore;
        this.executorService = executorService;
        this.continuationStore = continuationStore;
        this.expectedResponsesStore = expectedResponsesStore;
    }

    @Override
    public void reply(final String uuid, final String reply) {
        replyChannelStore.reply(uuid, reply);
    }

    @Override
    public void continueAsync(final String responseCorrelationId, final Consumer<String> consumer) {
        Continuation earlyResponseContinuation = expectedResponsesStore.addExpectedResponse(
                responseCorrelationId,
                new Continuation(consumer)
        );
        if (earlyResponseContinuation != null) {
            continueEarlyResponse(responseCorrelationId, consumer, earlyResponseContinuation);
        } else {
            logAddExpectedResponse(responseCorrelationId);
        }
    }

    private void continueEarlyResponse(
            final String responseCorrelationId,
            final Consumer<String> consumer,
            final Continuation earlyResponseContinuation
    ) {
        expectedResponsesStore.removeExpectedResponse(responseCorrelationId);
        log.info("Submit early response (responseCorrelationId={}).", responseCorrelationId);
        final Future<?> submit = executorService.submit(() -> {
                    log.info("Continue early response (responseCorrelationId={}).", responseCorrelationId);
                    final String response = earlyResponseContinuation.response();
                    log.trace("response={}", response);
                    consumer.accept(response);
                }
        );
        continuationStore.addFuture(submit, earlyResponseContinuation);
    }

    private static void logAddExpectedResponse(final String responseCorrelationId) {
        log.info(
                "Added expected response (responseCorrelationId={}) to continuation store for async continuation.",
                responseCorrelationId
        );
    }

    @Override
    public String request(final String channelName, final String request) {
        final String responseCorrelationId = UUID.randomUUID().toString();
        requestChannelStore.request(channelName, request, responseCorrelationId);
        return responseCorrelationId;
    }

    @Override
    public void event(final String channelName, final String event) {
        eventChannelStore.event(channelName, event);
    }
}
