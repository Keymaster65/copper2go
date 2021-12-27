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
import io.github.keymaster65.copper2go.engine.Engine;
import io.github.keymaster65.copper2go.engine.EngineRuntimeException;
import io.github.keymaster65.copper2go.engine.RequestChannel;
import io.github.keymaster65.copper2go.workflowapi.RequestChannelStore;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RequestChannelStoreImpl implements RequestChannelStore {
    private final Map<String, RequestChannel> requestChannelMap = new ConcurrentHashMap<>();

    public RequestChannelStoreImpl(
            final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs,
            final Engine engine
    ) {
        Objects.requireNonNull(engine, "Engine must be not null.");

        putHttpRequestChannels(httpRequestChannelConfigs, engine);
    }

    @Override
    public void request(
            final String channelName,
            final String request,
            Map<String,String> attributes,
            final String responseCorrelationId
    ) {
        Objects.requireNonNull(requestChannelMap.get(channelName), String.format("Channel with name %s%s", channelName, " must not be null."));
        requestChannelMap.get(channelName).request(request, attributes, responseCorrelationId);
    }

    public void close() {
        requestChannelMap.values().forEach(RequestChannel::close);
    }

    public void addKafkaRequestChannels(
            final String kafkaHost,
            final int kafkaPort,
            final Map<String, KafkaRequestChannelConfig> kafkaRequestChannelConfigs,
            final Engine engine
    ) {
        putKafkaRequestChannels(kafkaHost, kafkaPort, kafkaRequestChannelConfigs, engine);
    }

    private void putHttpRequestChannels(final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs, final Engine engine) {
        if (httpRequestChannelConfigs != null) {
            for (Map.Entry<String, HttpRequestChannelConfig> entry : httpRequestChannelConfigs.entrySet()) {
                HttpRequestChannelConfig config = entry.getValue();
                if (requestChannelMap.putIfAbsent(entry.getKey(),
                        new HttpRequestChannelImpl(
                                config.method,
                                new VertxHttpClient(
                                        config.host,
                                        config.port,
                                        config.path,
                                        engine
                                ))) != null) {
                    // should not happen
                    throw new EngineRuntimeException(String.format("Duplicate RequestChannel %s found.", entry.getKey()));
                }
            }
        }
    }

    private void putKafkaRequestChannels(final String kafkaHost, final int kafkaPort, final Map<String, KafkaRequestChannelConfig> kafkaRequestChannelConfigs, final Engine engine) {
        if (kafkaRequestChannelConfigs != null) {
            for (Map.Entry<String, KafkaRequestChannelConfig> entry : kafkaRequestChannelConfigs.entrySet()) {
                KafkaRequestChannelConfig config = entry.getValue();
                putKafkaRequestChannel(
                        entry.getKey(),
                        new KafkaRequestChannelImpl(
                                new Copper2GoKafkaSenderImpl(
                                        kafkaHost,
                                        kafkaPort,
                                        config.topic,
                                        RequestChannelStoreImpl::createKafkaProducer
                                ), engine));
            }
        }
    }

    void putKafkaRequestChannel(final String name, final KafkaRequestChannelImpl kafkaRequestChannelImpl) {
        if (requestChannelMap.putIfAbsent(name, kafkaRequestChannelImpl) != null) {
            throw new EngineRuntimeException(String.format("Duplicate RequestChannel %s found.", name));
        }
    }

    static KafkaProducer<String, String> createKafkaProducer(Map<String, String> config) {
        return KafkaProducer.create(Vertx.vertx(), config);
    }

}
