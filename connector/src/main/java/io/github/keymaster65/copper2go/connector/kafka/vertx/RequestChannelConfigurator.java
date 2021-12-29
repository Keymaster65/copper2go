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

import io.github.keymaster65.copper2go.connectorapi.DefaultRequestChannelStore;
import io.github.keymaster65.copper2go.connectorapi.ResponseReceiver;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;

import java.util.Map;

public class RequestChannelConfigurator {
    private RequestChannelConfigurator() {}

    public static void addKafkaRequestChannels(
            final String kafkaHost,
            final int kafkaPort,
            final Map<String, KafkaRequestChannelConfig> kafkaRequestChannelConfigs,
            final ResponseReceiver responseReceiver,
            final DefaultRequestChannelStore defaultRequestChannelStore

    ) {
        if (kafkaRequestChannelConfigs != null) {
            for (Map.Entry<String, KafkaRequestChannelConfig> entry : kafkaRequestChannelConfigs.entrySet()) {
                KafkaRequestChannelConfig config = entry.getValue();
                defaultRequestChannelStore.put(
                        entry.getKey(),
                        new KafkaRequestChannelImpl(
                                new Copper2GoKafkaSenderImpl(
                                        kafkaHost,
                                        kafkaPort,
                                        config.topic,
                                        RequestChannelConfigurator::createKafkaProducer
                                ), responseReceiver));
            }
        }
    }

    public static KafkaProducer<String, String> createKafkaProducer(Map<String, String> config) {
        return KafkaProducer.create(Vertx.vertx(), config);
    }
}
