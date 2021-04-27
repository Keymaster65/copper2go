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

import io.vertx.core.Future;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

public class Copper2GoKafkaSenderImpl implements Copper2GoKafkaSender {

    private final KafkaProducer<String, String> producer;
    private final String topic;

    public Copper2GoKafkaSenderImpl(
            final String host,
            final int port,
            final String topic,
            Function<Map<String, String>, KafkaProducer<String, String>> producerFactory
    ) {
        this.topic = topic;

        Map<String, String> config = new HashMap<>();
        config.put(BOOTSTRAP_SERVERS_CONFIG, host + ":" + port);
        config.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        config.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        config.put(ACKS_CONFIG, "1");
        config.put(RETRIES_CONFIG, "100");

        this.producer = producerFactory.apply(config);
    }

    public Future<RecordMetadata> send(final String request) {
        KafkaProducerRecord<String, String> event = KafkaProducerRecord.create(topic, request);
        return producer.send(event);
    }

    public void close() {
        producer.close();
    }
}
