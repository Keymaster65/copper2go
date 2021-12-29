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

import io.github.keymaster65.copper2go.engine.EngineControl;
import io.github.keymaster65.copper2go.connectorapi.PayloadReceiver;
import io.github.keymaster65.copper2go.connectorapi.ResponseReceiver;
import io.github.keymaster65.copper2go.engine.WorkflowRepositoryConfig;

public class Copper2GoEngine {

    private final PayloadReceiver payloadReceiver;
    private final ResponseReceiver responseReceiver;
    private final EngineControlImpl engineControl;

    public Copper2GoEngine(
            final int availableTickets,
            final WorkflowRepositoryConfig workflowRepositoryConfig,
            final ReplyChannelStoreImpl replyChannelStore
    ) {
        this.engineControl = new EngineControlImpl(availableTickets, workflowRepositoryConfig);
        this.payloadReceiver = new PayloadReceiverImpl(engineControl.scottyEngine, replyChannelStore);
        this.responseReceiver = new ResponseReceiverImpl(engineControl.scottyEngine);
    }

    Copper2GoEngine(
            final PayloadReceiver payloadReceiver,
            final ResponseReceiver responseReceiver,
            final EngineControlImpl engineControl
    ) {
        this.payloadReceiver = payloadReceiver;
        this.responseReceiver = responseReceiver;
        this.engineControl = engineControl;
    }

    public EngineControl getEngineControl() {
        return engineControl;
    }

    public PayloadReceiver getPayloadReceiver() {
        return payloadReceiver;
    }

    public ResponseReceiver getResponseReceiver() {
        return responseReceiver;
    }

}
