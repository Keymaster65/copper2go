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
package io.github.keymaster65.copper2go.connector.kafka.vertx;

import io.github.keymaster65.copper2go.api.connector.DefaultRequestChannelStore;
import io.github.keymaster65.copper2go.api.connector.ResponseReceiver;
import net.jqwik.api.Example;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.config.ConfigException;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

class RequestChannelConfiguratorTest {

    public static final String CHANNEL_NAME = "channelName";

    @Example
    void putKafkaRequestChannelsBadHost() {
        final ResponseReceiver responseReceiver = Mockito.mock(ResponseReceiver.class);
        final DefaultRequestChannelStore defaultRequestChannelStore = new DefaultRequestChannelStore();
        final Map<String, KafkaRequestChannelConfig> kafkaRequestChannelConfigs = new HashMap<>();
        kafkaRequestChannelConfigs.put(
                CHANNEL_NAME,
                new KafkaRequestChannelConfig("topic"));

        Assertions.assertThatCode(() ->
                        RequestChannelConfigurator.addKafkaRequestChannels(
                                "kafkaHost",
                                0,
                                kafkaRequestChannelConfigs,
                                responseReceiver,
                                defaultRequestChannelStore
                        ))
                .isInstanceOf(KafkaException.class)
                .hasMessage("Failed to construct kafka producer")
                .hasRootCauseInstanceOf(ConfigException.class)
                .hasRootCauseMessage("No resolvable bootstrap urls given in bootstrap.servers");

    }

    @Example
    void putKafkaRequestChannelsLocalhost() {
        final ResponseReceiver responseReceiver = Mockito.mock(ResponseReceiver.class);
        final DefaultRequestChannelStore defaultRequestChannelStore = new DefaultRequestChannelStore();
        final Map<String, KafkaRequestChannelConfig> kafkaRequestChannelConfigs = new HashMap<>();
        kafkaRequestChannelConfigs.put(
                CHANNEL_NAME,
                new KafkaRequestChannelConfig("topic"));

        Assertions.assertThatCode(() ->
                        RequestChannelConfigurator.addKafkaRequestChannels(
                                "localhost",
                                58888,
                                kafkaRequestChannelConfigs,
                                responseReceiver,
                                defaultRequestChannelStore
                        ))
                .doesNotThrowAnyException();
    }

    @Example
    void putKafkaRequestChannelsNull() {
        final ResponseReceiver responseReceiver = Mockito.mock(ResponseReceiver.class);
        final DefaultRequestChannelStore defaultRequestChannelStore = new DefaultRequestChannelStore();

        Assertions.assertThatCode(() ->
                        RequestChannelConfigurator.addKafkaRequestChannels(
                                "kafkaHost",
                                0,
                                null,
                                responseReceiver,
                                defaultRequestChannelStore
                        ))
                .doesNotThrowAnyException();
    }

    @Example
    void createKafkaProducer() {
        final Map<String, String> emptyMap = Map.of();

        Assertions.assertThatCode(() ->
                        RequestChannelConfigurator.createKafkaProducer(emptyMap)
                )
                .isInstanceOf(ConfigException.class)
                .hasMessage("Missing required configuration \"key.serializer\" which has no default value.");
    }
}