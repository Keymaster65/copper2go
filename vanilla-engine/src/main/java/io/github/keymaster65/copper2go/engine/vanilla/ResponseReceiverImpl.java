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

import io.github.keymaster65.copper2go.api.connector.ResponseReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

public class ResponseReceiverImpl implements ResponseReceiver {
    private final VanillaEngineImpl vanillaEngineImpl;

    private static final Logger log = LoggerFactory.getLogger(ResponseReceiverImpl.class);

    public ResponseReceiverImpl(final VanillaEngineImpl vanillaEngineImpl) {
        this.vanillaEngineImpl = vanillaEngineImpl;
    }

    @Override
    public void receive(final String responseCorrelationId, final String response) {
        final Continuation responseContinuation = new Continuation(response);
        Continuation waitingConsumer = vanillaEngineImpl.continuationStore.addExpectedResponse(responseCorrelationId, responseContinuation);
        if (waitingConsumer != null) {
            consumeResponse(responseCorrelationId, response, waitingConsumer);
        } else {
            handleEarlyResonse(responseCorrelationId, responseContinuation);
        }
    }

    @Override
    public void receiveError(final String responseCorrelationId, final String response) {
        receive(responseCorrelationId, response);
    }

    private static void handleEarlyResonse(final String responseCorrelationId, final Continuation responseContinuation) {
        log.info(
                "Receive early response (responseCorrelationId={}). Add Continuation {}.",
                responseCorrelationId,
                responseContinuation
        );
    }

    private void consumeResponse(final String responseCorrelationId, final String response, final Continuation waitingConsumer) {
        log.info("Receive response (responseCorrelationId={}) for waitingConsumer Continuation {}.", responseCorrelationId, waitingConsumer);
        log.trace("response={}", response);
        final Continuation continuation = vanillaEngineImpl.continuationStore.removeExpectedResponse(responseCorrelationId);
        log.debug("Remove expected response {}.", continuation);
        final Future<?> submit = vanillaEngineImpl.executorService.submit(() -> {
                    log.info("Continue response (responseCorrelationId={}).", responseCorrelationId);
                    log.trace("response={}", response);
                    waitingConsumer.consumer().accept(response);
                }
        );
        vanillaEngineImpl.continuationStore.addFuture(submit, waitingConsumer);
    }
}
