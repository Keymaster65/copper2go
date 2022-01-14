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
package io.github.keymaster65.copper2go.engine;

import io.github.keymaster65.copper2go.api.connector.PayloadReceiver;
import io.github.keymaster65.copper2go.api.connector.ResponseReceiver;

public class Copper2GoEngine {

    private final PayloadReceiver payloadReceiver;
    private final ResponseReceiver responseReceiver;
    private final EngineControl engineControl;

    public Copper2GoEngine(
            final PayloadReceiver payloadReceiver,
            final ResponseReceiver responseReceiver,
            final EngineControl engineControl
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
