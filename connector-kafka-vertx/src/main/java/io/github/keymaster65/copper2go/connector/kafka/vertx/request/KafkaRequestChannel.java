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
package io.github.keymaster65.copper2go.connector.kafka.vertx.request;

import io.github.keymaster65.copper2go.api.connector.RequestChannel;
import io.github.keymaster65.copper2go.api.connector.ResponseReceiver;
import io.vertx.core.Future;
import io.vertx.kafka.client.producer.RecordMetadata;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class KafkaRequestChannel implements RequestChannel, AutoCloseable {

    private final KafkaSender kafkaSender;
    private final ResponseReceiver responseReceiver;

    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failCount = new AtomicLong(0);

    public KafkaRequestChannel(
            final KafkaSender kafkaSender,
            final ResponseReceiver responseReceiver
    ) {
        this.kafkaSender = kafkaSender;
        this.responseReceiver = responseReceiver;
    }

    @Override
    public void request(
            final String request,
            final Map<String, String> attributes,
            final String responseCorrelationId
    ) {
        final Future<RecordMetadata> send = kafkaSender.send(request, attributes);
        send
                .onSuccess(metadata -> handleSendSuccess(responseCorrelationId, send))
                .onFailure(throwable -> handleSendFailure(responseCorrelationId, throwable));
    }

    void handleSendFailure(final String responseCorrelationId, final Throwable throwable) {
        responseReceiver.receiveError(responseCorrelationId, throwable.getMessage());
        failCount.incrementAndGet();
    }

    void handleSendSuccess(final String responseCorrelationId, final Future<RecordMetadata> send) {
        responseReceiver.receive(responseCorrelationId, createResponse(send));
        successCount.incrementAndGet();
    }

    static String createResponse(final Future<RecordMetadata> send) {
        if (send.result() == null) {
            return "{}";
        }
        return send.result().toJson().encode();
    }

    @Override
    public void close() {
        kafkaSender.close();
    }

    public long getSuccessCount() {
        return successCount.get();
    }

    public long getFailCount() {
        return failCount.get();
    }
}
