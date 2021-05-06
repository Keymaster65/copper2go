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
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.EngineRuntimeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

class RequestChannelStoreImplTest {

    @Test
    void addDuplicateRequestChannels() {
        Copper2GoEngine engine = mock(Copper2GoEngine.class);

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

        final Map<String, KafkaRequestChannelConfig> kafkaRequestChannelConfigs = new HashMap<>();
        kafkaRequestChannelConfigs.put(
                channelName,
                new KafkaRequestChannelConfig("topic"));

        RequestChannelStoreImpl requestChannelStore = new RequestChannelStoreImpl(httpRequestChannelConfigs, engine);

        Assertions.assertThatExceptionOfType(EngineRuntimeException.class).isThrownBy(() ->
                requestChannelStore.putKafkaRequestChannel(channelName, mock(KafkaRequestChannelImpl.class))
        );
    }
}