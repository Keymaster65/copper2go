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
    private final Copper2GoEngine copper2GoEngine;
    private final String workflowName;
    private final long majorVersion;
    private final long minorVersion;

    public KafkaConsumerHandler(
            final String topic,
            final Copper2GoEngine copper2GoEngine,
            final String workflowName,
            final long majorVersion,
            final long minorVersion
            ) {
        this.topic = topic;

        this.copper2GoEngine = copper2GoEngine;
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
            copper2GoEngine.callWorkflow(
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
            return null;
        }
        var attributes = new HashMap<String, String>();
        headers.iterator()
                .forEachRemaining(entry ->
                        attributes.put(entry.key(), entry.value().toString(StandardCharsets.UTF_8))
                );
        return attributes;
    }
}
