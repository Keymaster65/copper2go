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
package io.github.keymaster65.copper2go.engine.impl;

import io.github.keymaster65.copper2go.api.connector.ResponseReceiver;
import org.copperengine.core.Acknowledge;
import org.copperengine.core.Response;
import org.copperengine.core.tranzient.TransientScottyEngine;

public class ResponseReceiverImpl implements ResponseReceiver {

    private final TransientScottyEngine scottyEngine;

    ResponseReceiverImpl(final TransientScottyEngine scottyEngine) {
        this.scottyEngine = scottyEngine;
    }

    @Override
    public void receive(final String responseCorrelationId, final String response) {
        Response<String> copperResponse = new Response<>(responseCorrelationId, response, null);
        scottyEngine.notify(copperResponse, new Acknowledge.BestEffortAcknowledge());
    }

    @Override
    public void receiveError(final String responseCorrelationId, final String response) {
        Response<String> copperResponse = new Response<>(responseCorrelationId, response, new RuntimeException(response));
        scottyEngine.notify(copperResponse, new Acknowledge.BestEffortAcknowledge());
    }

}
