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

import io.github.keymaster65.copper2go.connectorapi.PayloadReceiver;
import io.vertx.core.Handler;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import io.vertx.kafka.client.producer.KafkaHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class KafkaConsumerHandler implements Handler<KafkaConsumerRecord<String, String>>  {

    private final String topic;

    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failCount = new AtomicLong(0);

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerHandler.class);
    private final PayloadReceiver payloadReceiver;
    private final String workflowName;
    private final long majorVersion;
    private final long minorVersion;

    public KafkaConsumerHandler(
            final String topic,
            final PayloadReceiver payloadReceiver,
            final String workflowName,
            final long majorVersion,
            final long minorVersion
            ) {
        this.topic = topic;

        this.payloadReceiver = payloadReceiver;
        this.workflowName = workflowName;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public long getSuccessCount() {
        return successCount.get();
    }

    public long getFailCount() {
        return failCount.get();
    }

    @Override
    public void handle(final KafkaConsumerRecord<String, String> event) {
        try {
            log.info("Call workflow {} for topic {}.", this.workflowName, topic);
            payloadReceiver.receive(
                    event.value(),
                    createAttributes(event.headers()),
                    null,
                    workflowName,
                    majorVersion,
                    minorVersion);
            successCount.incrementAndGet();
        } catch (Exception e) {
            failCount.incrementAndGet();
            log.error("Unable to call Workflow from topic {} with payload='{}'.", topic, event.value(), e);
        }
    }

    static Map<String,String> createAttributes(final List<KafkaHeader> headers) {
        if (headers == null || headers.isEmpty()) {
            return Map.of();
        }
        Map<String,String> attributes = new HashMap<>();
        headers.iterator()
                .forEachRemaining(entry ->
                        attributes.put(entry.key(), entry.value().toString(StandardCharsets.UTF_8))
                );
        return attributes;
    }
}
