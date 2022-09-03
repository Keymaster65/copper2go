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
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.impl.EngineControlImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class Copper2GoEngineTest {

    @Test
    void getPayloadReceiver() {
        final PayloadReceiver payloadReceiver = Mockito.mock(PayloadReceiver.class);
        final Copper2GoEngine copper2GoEngine = new Copper2GoEngine(
                payloadReceiver,
                Mockito.mock(ResponseReceiver.class),
                Mockito.mock(EngineControlImpl.class)
        );

        Assertions.assertThat(copper2GoEngine.payloadReceiver()).isSameAs(payloadReceiver);
    }

    @Test
    void getResponseReceiver() {
        final ResponseReceiver responseReceiver = Mockito.mock(ResponseReceiver.class);
        final Copper2GoEngine copper2GoEngine = new Copper2GoEngine(
                Mockito.mock(PayloadReceiver.class),
                responseReceiver,
                Mockito.mock(EngineControlImpl.class)
        );

        Assertions.assertThat(copper2GoEngine.responseReceiver()).isSameAs(responseReceiver);
    }

    @Test
    void getEngineControl() {
        final EngineControlImpl engineControl = Mockito.mock(EngineControlImpl.class);
        final Copper2GoEngine copper2GoEngine = new Copper2GoEngine(
                Mockito.mock(PayloadReceiver.class),
                Mockito.mock(ResponseReceiver.class),
                engineControl
        );

        Assertions.assertThat(copper2GoEngine.engineControl()).isSameAs(engineControl);
    }
}