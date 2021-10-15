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
import io.github.keymaster65.copper2go.engine.RequestChannel;
import io.vertx.core.Future;
import io.vertx.kafka.client.producer.RecordMetadata;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class KafkaRequestChannelImpl implements RequestChannel {

    private final Copper2GoKafkaSender copper2GoKafkaSender;
    private final Copper2GoEngine engine;

    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failCount = new AtomicLong(0);

    public KafkaRequestChannelImpl(
            final Copper2GoKafkaSender copper2GoKafkaSender,
            final Copper2GoEngine engine
    ) {
        this.copper2GoKafkaSender = copper2GoKafkaSender;
        this.engine = engine;
    }

    @Override
    public void request(
            final String request,
            final Map<String, String> attributes,
            final String responseCorrelationId
    ) {
        final Future<RecordMetadata> send = copper2GoKafkaSender.send(request, attributes);
        send
                .onSuccess(metadata -> handleSendSuccess(responseCorrelationId, send))
                .onFailure(throwable -> handleSendFailure(responseCorrelationId, throwable));
    }

    void handleSendFailure(final String responseCorrelationId, final Throwable throwable) {
        engine.notifyError(responseCorrelationId, throwable.getMessage());
        failCount.incrementAndGet();
    }

    void handleSendSuccess(final String responseCorrelationId, final Future<RecordMetadata> send) {
        engine.notify(responseCorrelationId, createResponse(send));
        successCount.incrementAndGet();
    }

    private String createResponse(final Future<RecordMetadata> send) {
        if (send.result() == null) {
            return "{}";
        }
        return send.result().toJson().encode();
    }

    @Override
    public void close() {
        copper2GoKafkaSender.close();
    }

    public long getSuccessCount() {
        return successCount.get();
    }

    public long getFailCount() {
        return failCount.get();
    }
}
