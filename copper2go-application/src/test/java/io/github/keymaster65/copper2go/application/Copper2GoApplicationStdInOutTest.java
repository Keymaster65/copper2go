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
package io.github.keymaster65.copper2go.application;

import io.github.keymaster65.copper2go.api.connector.DefaultRequestChannelStore;
import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpServer;
import io.github.keymaster65.copper2go.connector.kafka.vertx.receiver.KafkaReceiver;
import io.github.keymaster65.copper2go.connector.standardio.StandardInOutException;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.EngineControl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

class Copper2GoApplicationStdInOutTest {

    @Test
    void start() {
        final EngineControl engineControl = Mockito.mock(EngineControl.class);
        final Copper2GoEngine copper2GoEngine = Mockito.mock(Copper2GoEngine.class);
        Mockito.when(copper2GoEngine.engineControl()).thenReturn(engineControl);
        final Copper2GoHttpServer httpServer = Mockito.mock(Copper2GoHttpServer.class);
        final Map<String, KafkaReceiver> kafkaReceiverMap = Map.of();

        final Copper2GoApplication application = new Copper2GoApplication(
                copper2GoEngine,
                httpServer,
                Mockito.mock(DefaultRequestChannelStore.class),
                kafkaReceiverMap
        );

        Assertions.assertThatCode(application::startWithStdInOut)
                .isInstanceOf(StandardInOutException.class)
                .hasMessage("Exception while getting input.")
                .hasRootCauseInstanceOf(NullPointerException.class)
                .hasRootCauseMessage("Read a 'null' line. So there seems to be no stdin. Might happen when starting with gradle.");
    }

}