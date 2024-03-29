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
package io.github.keymaster65.copper2go.connector.kafka.vertx.receiver;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class KafkaReceiver implements AutoCloseable {

    private final KafkaConsumer<String, String> consumer;
    private final String topic;

    private static final Logger log = LoggerFactory.getLogger(KafkaReceiver.class);

    public KafkaReceiver(
            final String host,
            final int port,
            final String topic,
            final String groupId,
            final Handler<KafkaConsumerRecord<String, String>> handler
    ) {
        this(topic, createConsumer(host, port, groupId, handler));
    }

    public KafkaReceiver(
            final String topic,
            final KafkaConsumer<String, String> consumer
    ) {
        this.topic = topic;
        this.consumer = consumer;
    }

    private static KafkaConsumer<String, String> createConsumer(
            final String host,
            final int port,
            final String groupId,
            final Handler<KafkaConsumerRecord<String, String>> handler
    ) {
        Map<String, String> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, host + ":" + port);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = KafkaConsumer.create(Vertx.vertx(), config);
        consumer.handler(handler);

        return consumer;
    }

    public void start() {
        log.info("Start receiving from topic {}.", topic);
        consumer.subscribe(topic);
    }

    @Override
    public void close() {
        log.info("Finish receiving from topic {}.", topic);
        consumer.commit();
        consumer.unsubscribe();
        consumer.close();
    }
}
