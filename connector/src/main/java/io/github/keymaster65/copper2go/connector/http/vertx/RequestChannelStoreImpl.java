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
import io.github.keymaster65.copper2go.connector.kafka.vertx.Copper2GoKafkaSenderImpl;
import io.github.keymaster65.copper2go.connector.kafka.vertx.KafkaRequestChannelConfig;
import io.github.keymaster65.copper2go.connector.kafka.vertx.KafkaRequestChannelImpl;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.RequestChannel;
import io.github.keymaster65.copper2go.workflowapi.RequestChannelStore;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestChannelStoreImpl implements RequestChannelStore {
    private static Map<String, RequestChannel> requestChannelMap = new ConcurrentHashMap<>();

    public RequestChannelStoreImpl(
            final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs,
            final Copper2GoEngine engine
    ) {
        putHttpRequestChannels(httpRequestChannelConfigs, engine);
    }

    @Override
    public void request(final String channelName, final String request, final String responseCorrelationId) {
        requestChannelMap.get(channelName).request(request, responseCorrelationId);
    }

    public void close() {
        requestChannelMap.values().forEach(RequestChannel::close);
    }

    public void addKafkaRequestChannels(
            final String kafkaHost,
            final int kafkaPort,
            final Map<String, KafkaRequestChannelConfig> kafkaRequestChannelConfigs,
            final Copper2GoEngine engine
    ) {
        putKafkaRequestChannels(kafkaHost, kafkaPort, kafkaRequestChannelConfigs, engine);
    }

    private void putHttpRequestChannels(final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs, final Copper2GoEngine engine) {
        if (httpRequestChannelConfigs != null) {
            for (Map.Entry<String, HttpRequestChannelConfig> entry : httpRequestChannelConfigs.entrySet()) {
                HttpRequestChannelConfig config = entry.getValue();
                requestChannelMap.put(entry.getKey(),
                        new HttpRequestChannelImpl(
                                config.method,
                                new VertxHttpClient(
                                        config.host,
                                        config.port,
                                        config.path,
                                        engine
                                )));
            }
        }
    }

    private void putKafkaRequestChannels(final String kafkaHost, final int kafkaPort, final Map<String, KafkaRequestChannelConfig> kafkaRequestChannelConfigs, final Copper2GoEngine engine) {
        if (kafkaRequestChannelConfigs != null) {
            for (Map.Entry<String, KafkaRequestChannelConfig> entry : kafkaRequestChannelConfigs.entrySet()) {
                KafkaRequestChannelConfig config = entry.getValue();
                requestChannelMap.put(entry.getKey(),
                        new KafkaRequestChannelImpl(
                                new Copper2GoKafkaSenderImpl(
                                        kafkaHost,
                                        kafkaPort,
                                        config.topic,
                                        RequestChannelStoreImpl::apply
                                ), engine));
            }
        }
    }

    private static KafkaProducer<String, String> apply(Map<String, String> config) {
        return KafkaProducer.create(Vertx.vertx(), config);
    }

}
