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
package io.github.keymaster65.copper2go.connector.http.vertx;

import io.github.keymaster65.copper2go.connector.http.HttpRequestChannelConfig;
import io.github.keymaster65.copper2go.connector.kafka.vertx.KafkaRequestChannelConfig;
import io.github.keymaster65.copper2go.connector.kafka.vertx.KafkaRequestChannelImpl;
import io.github.keymaster65.copper2go.engine.Engine;
import io.github.keymaster65.copper2go.engine.EngineRuntimeException;
import net.jqwik.api.Example;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.config.ConfigException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

class RequestChannelStoreImplTest {

    @Example
    void addDuplicateRequestChannels() {
        Engine engine = Mockito.mock(Engine.class);
        final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs = new HashMap<>();
        final String channelName = "channelName";
        httpRequestChannelConfigs.put(
                channelName,
                new HttpRequestChannelConfig(
                        "httpHost",
                        0,
                        "/",
                        "GET"
                ));
        RequestChannelStoreImpl requestChannelStore = new RequestChannelStoreImpl(httpRequestChannelConfigs, engine);

        Assertions.assertThatExceptionOfType(EngineRuntimeException.class).isThrownBy(() ->
                requestChannelStore.putKafkaRequestChannel(channelName, Mockito.mock(KafkaRequestChannelImpl.class))
        );
    }

    @Example
    void addRequestChannels() {
        RequestChannelStoreImpl requestChannelStore = createHttpRequestChannelStore(Mockito.mock(Engine.class));

        final String channelName2 = "channelName2";
        Assertions.assertThatCode(() ->
                        requestChannelStore.putKafkaRequestChannel(channelName2, Mockito.mock(KafkaRequestChannelImpl.class))
                )
                .doesNotThrowAnyException();
    }

    @Example
    void request() {
        Engine engine = Mockito.mock(Engine.class);
        final String channelName = "channelName";
        RequestChannelStoreImpl requestChannelStore = createHttpRequestChannelStore(engine);

        requestChannelStore.request(channelName, "request", "responseCorrelationId");
        LockSupport.parkNanos(6L * 1000 * 1000 * 1000);

        Mockito.verify(engine).receiveError(Mockito.any(), Mockito.any());
    }

    @Example
    void putKafkaRequestChannels() {
        Engine engine = Mockito.mock(Engine.class);
        RequestChannelStoreImpl requestChannelStore = new RequestChannelStoreImpl(null, engine);
        final String channelName = "channelName";
        final Map<String, KafkaRequestChannelConfig> kafkaRequestChannelConfigs = new HashMap<>();
        kafkaRequestChannelConfigs.put(
                channelName,
                new KafkaRequestChannelConfig("topic"));

        Assertions.assertThatCode(() ->
                        requestChannelStore.addKafkaRequestChannels(
                                "kafkaHost",
                                0,
                                kafkaRequestChannelConfigs,
                                engine
                        ))
                .isInstanceOf(KafkaException.class)
                .hasMessage("Failed to construct kafka producer")
                .hasRootCauseInstanceOf(ConfigException.class)
                .hasRootCauseMessage("No resolvable bootstrap urls given in bootstrap.servers");

    }

    @Example
    void putKafkaRequestChannelsNull() {
        Engine engine = Mockito.mock(Engine.class);
        RequestChannelStoreImpl requestChannelStore = createEmptyRequestChannelStore(engine);

        Assertions.assertThatCode(() ->
                        requestChannelStore.addKafkaRequestChannels(
                                "kafkaHost",
                                0,
                                null,
                                engine
                        ))
                .doesNotThrowAnyException();
    }

    @Example
    void close() {
        RequestChannelStoreImpl requestChannelStore = createEmptyRequestChannelStore(Mockito.mock(Engine.class));

        Assertions.assertThatCode(requestChannelStore::close)
                .doesNotThrowAnyException();

    }


    @Test
    void createKafkaProducer() {
        Map<String, String> emptyMap = Map.of();
        Assertions.assertThatCode(() ->
                        RequestChannelStoreImpl.createKafkaProducer(emptyMap)
                )
                .isInstanceOf(ConfigException.class)
                .hasMessage("Missing required configuration \"key.serializer\" which has no default value.");
    }

    private RequestChannelStoreImpl createHttpRequestChannelStore(final Engine engine) {
        final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs = new HashMap<>();
        final String channelName = "channelName";
        httpRequestChannelConfigs.put(
                channelName,
                new HttpRequestChannelConfig(
                        "httpHost",
                        0,
                        "/",
                        "GET"
                ));
        return new RequestChannelStoreImpl(httpRequestChannelConfigs, engine);
    }

    private RequestChannelStoreImpl createEmptyRequestChannelStore(final Engine engine) {
        return new RequestChannelStoreImpl(null, engine);
    }
}