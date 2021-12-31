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
import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.application.config.Config;
import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpServer;
import io.github.keymaster65.copper2go.connector.kafka.vertx.Copper2GoKafkaReceiverImpl;
import io.github.keymaster65.copper2go.connector.standardio.StandardInOutException;
import io.github.keymaster65.copper2go.engine.EngineControl;
import io.github.keymaster65.copper2go.engine.impl.Copper2GoEngine;
import org.assertj.core.api.Assertions;
import org.copperengine.core.DependencyInjector;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Map;

class ApplicationTest {


    @Test
    void start() throws EngineException {
        final EngineControl engineControl = Mockito.mock(EngineControl.class);
        final Copper2GoEngine copper2GoEngine = Mockito.mock(Copper2GoEngine.class);
        Mockito.when(copper2GoEngine.getEngineControl()).thenReturn(engineControl);
        final DependencyInjector dependencyInjector = Mockito.mock(DependencyInjector.class);
        final Copper2GoHttpServer httpServer = Mockito.mock(Copper2GoHttpServer.class);
        final DefaultRequestChannelStore defaultRequestChannelStore = Mockito.mock(DefaultRequestChannelStore.class);
        final Copper2GoKafkaReceiverImpl kafkaReceiver = Mockito.mock(Copper2GoKafkaReceiverImpl.class);
        final Map<String, Copper2GoKafkaReceiverImpl> kafkaReceiverMap = Map.of("name", kafkaReceiver);

        final Application application = new Application(
                copper2GoEngine,
                dependencyInjector,
                httpServer,
                defaultRequestChannelStore,
                kafkaReceiverMap
        );

        Assertions.assertThatCode(application::start)
                .doesNotThrowAnyException();

        Assertions.assertThat(application.isStopRequested()).isFalse();
        Mockito.verify(engineControl).start(dependencyInjector);
        Mockito.verify(httpServer).start();
        Mockito.verify(kafkaReceiver).start();
    }

    @Test
    void startWithStdInOut() {
        final EngineControl engineControl = Mockito.mock(EngineControl.class);
        final Copper2GoEngine copper2GoEngine = Mockito.mock(Copper2GoEngine.class);
        Mockito.when(copper2GoEngine.getEngineControl()).thenReturn(engineControl);
        final Copper2GoHttpServer httpServer = Mockito.mock(Copper2GoHttpServer.class);
        final Map<String, Copper2GoKafkaReceiverImpl> kafkaReceiverMap = Map.of();

        final Application application = new Application(
                copper2GoEngine,
                Mockito.mock(DependencyInjector.class),
                httpServer,
                Mockito.mock(DefaultRequestChannelStore.class),
                kafkaReceiverMap
        );

        Assertions.assertThatCode(application::startWithStdInOut)
                .isInstanceOf (StandardInOutException.class)
                .hasMessage("Exception while getting input.")
                .hasRootCauseInstanceOf(NullPointerException.class)
                .hasRootCauseMessage("Read a 'null' line. So there seems to be no stdin. Might happen when starting with gradle.");
    }


    @Test
    void stop() throws EngineException {
        final EngineControl engineControl = Mockito.mock(EngineControl.class);
        final Copper2GoEngine copper2GoEngine = Mockito.mock(Copper2GoEngine.class);
        Mockito.when(copper2GoEngine.getEngineControl()).thenReturn(engineControl);
        final DependencyInjector dependencyInjector = Mockito.mock(DependencyInjector.class);
        final Copper2GoHttpServer httpServer = Mockito.mock(Copper2GoHttpServer.class);
        final DefaultRequestChannelStore defaultRequestChannelStore = Mockito.mock(DefaultRequestChannelStore.class);
        final Copper2GoKafkaReceiverImpl kafkaReceiver = Mockito.mock(Copper2GoKafkaReceiverImpl.class);
        final Map<String, Copper2GoKafkaReceiverImpl> kafkaReceiverMap = Map.of("name", kafkaReceiver);

        final Application application = new Application(
                copper2GoEngine,
                dependencyInjector,
                httpServer,
                defaultRequestChannelStore,
                kafkaReceiverMap
        );

        Assertions.assertThatCode(application::stop)
                .doesNotThrowAnyException();

        Assertions.assertThat(application.isStopRequested()).isTrue();
        Mockito.verify(engineControl).stop();
        Mockito.verify(httpServer).stop();
        Mockito.verify(kafkaReceiver).close();
        Mockito.verify(defaultRequestChannelStore).close();
    }

    @Test
    void stopWithHttpServerException() throws EngineException {
        final EngineControl engineControl = Mockito.mock(EngineControl.class);
        final Copper2GoEngine copper2GoEngine = Mockito.mock(Copper2GoEngine.class);
        Mockito.when(copper2GoEngine.getEngineControl()).thenReturn(engineControl);
        final DependencyInjector dependencyInjector = Mockito.mock(DependencyInjector.class);
        final Copper2GoHttpServer httpServer = Mockito.mock(Copper2GoHttpServer.class);
        final DefaultRequestChannelStore defaultRequestChannelStore = Mockito.mock(DefaultRequestChannelStore.class);
        final Copper2GoKafkaReceiverImpl kafkaReceiver = Mockito.mock(Copper2GoKafkaReceiverImpl.class);
        final Map<String, Copper2GoKafkaReceiverImpl> kafkaReceiverMap = Map.of("name", kafkaReceiver);
        Mockito.doThrow(new NullPointerException("Test")).when(httpServer).stop();

        final Application application = new Application(
                copper2GoEngine,
                dependencyInjector,
                httpServer,
                defaultRequestChannelStore,
                kafkaReceiverMap
        );

        Assertions.assertThatCode(application::stop)
                .doesNotThrowAnyException();

        Assertions.assertThat(application.isStopRequested()).isTrue();
        Mockito.verify(engineControl).stop();
        Mockito.verify(httpServer).stop();
        Mockito.verify(kafkaReceiver).close();
        Mockito.verify(defaultRequestChannelStore).close();
    }

    @Test
    void stopWithoutStart() throws IOException {
        final Application application = ApplicationFactory.of(Config.of());

        Assertions.assertThatCode(application::stop)
                .doesNotThrowAnyException();

        Assertions.assertThat(application.isStopRequested()).isTrue();
    }

    @Test
    void isStopRequestedWithOutStop() throws IOException {
        final Application application = ApplicationFactory.of(Config.of());

        Assertions.assertThat(application.isStopRequested()).isFalse();

    }
}