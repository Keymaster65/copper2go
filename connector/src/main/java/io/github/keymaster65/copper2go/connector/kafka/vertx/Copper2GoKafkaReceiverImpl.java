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

import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

public class Copper2GoKafkaReceiverImpl {

    private final KafkaConsumer<String, String> consumer;
    private final String topic;

    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failCount = new AtomicLong(0);

    private static final Logger log = LoggerFactory.getLogger(Copper2GoKafkaReceiverImpl.class);

    public Copper2GoKafkaReceiverImpl(
            final String host,
            final int port,
            final KafkaReceiverConfig receiverConfig,
            Copper2GoEngine copper2GoEngine
    ) {
        this.topic = receiverConfig.topic;

        Map<String, String> config = new HashMap<>();
        config.put(BOOTSTRAP_SERVERS_CONFIG, host + ":" + port);
        config.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        config.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        config.put(GROUP_ID_CONFIG, receiverConfig.groupId);
        config.put(AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = KafkaConsumer.create(Vertx.vertx(), config);

        consumer.handler(event ->
        {
            try {
                copper2GoEngine.callWorkflow(event.value(), null, receiverConfig.workflowName, receiverConfig.majorVersion, receiverConfig.minorVersion);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
                log.error("Unable to call Workflow with payload='{}'.", event.value(), e);
            }
        });
    }

    public void start() {
        consumer.subscribe(topic);
    }

    public void close() {
        consumer.close();
    }

    public long getSuccessCount() {
        return successCount.get();
    }

    public long getFailCount() {
        return failCount.get();
    }
}
