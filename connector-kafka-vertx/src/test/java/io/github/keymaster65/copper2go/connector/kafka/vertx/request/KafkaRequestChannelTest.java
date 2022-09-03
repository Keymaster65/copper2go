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

import io.github.keymaster65.copper2go.api.connector.ResponseReceiver;
import io.vertx.core.Future;
import io.vertx.kafka.client.producer.RecordMetadata;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class KafkaRequestChannelTest {

    public static final String CORR_ID = "corrId";
    public static final String REQUEST = "request";

    @Test
    void request() {
        ResponseReceiver responseReceiver = Mockito.mock(ResponseReceiver.class);
        KafkaSender sender = Mockito.mock(KafkaSender.class);
        @SuppressWarnings("unchecked")
        Future<RecordMetadata> metadata = Mockito.mock(Future.class);
        Mockito.when(sender.send(Mockito.eq(REQUEST), Mockito.any())).thenReturn(metadata);
        Mockito.when(metadata.onSuccess(Mockito.any())).thenReturn(metadata);
        Mockito.when(metadata.onFailure(Mockito.any())).thenReturn(metadata);

        KafkaRequestChannel requestChannel = new KafkaRequestChannel(
                sender,
                responseReceiver
        );

        requestChannel.request(REQUEST, CORR_ID);

        Mockito.verify(metadata).onSuccess(Mockito.any());
        Mockito.verify(metadata).onFailure(Mockito.any());
    }

    @Test
    void handleSendSuccess() {
        ResponseReceiver engine = Mockito.mock(ResponseReceiver.class);
        KafkaSender sender = Mockito.mock(KafkaSender.class);
        @SuppressWarnings("unchecked")
        Future<RecordMetadata> metadata = Mockito.mock(Future.class);

        KafkaRequestChannel requestChannel = new KafkaRequestChannel(
                sender,
                engine
        );

        requestChannel.handleSendSuccess(CORR_ID, metadata);

        Mockito.verify(engine).receive(CORR_ID, "{}");
        Assertions.assertThat(requestChannel.getSuccessCount()).isOne();
        Assertions.assertThat(requestChannel.getFailCount()).isZero();
    }

    @Test
    void handleSendFailure() {
        ResponseReceiver responseReceiver = Mockito.mock(ResponseReceiver.class);
        KafkaSender sender = Mockito.mock(KafkaSender.class);

        KafkaRequestChannel requestChannel = new KafkaRequestChannel(
                sender,
                responseReceiver
        );

        final RuntimeException testException = new RuntimeException("Test");
        requestChannel.handleSendFailure(CORR_ID, testException);

        Mockito.verify(responseReceiver).receiveError(CORR_ID, testException.getMessage());
        Assertions.assertThat(requestChannel.getSuccessCount()).isZero();
        Assertions.assertThat(requestChannel.getFailCount()).isOne();
    }

    @Test
    void createResponse() {
        @SuppressWarnings("unchecked")
        Future<RecordMetadata> metadata = Mockito.mock(Future.class);
        final RecordMetadata value = new RecordMetadata();
        Mockito.when(metadata.result()).thenReturn(value);

        final String response = KafkaRequestChannel.createResponse(metadata);

        Assertions.assertThat(response).isEqualTo("{\"offset\":0,\"partition\":0,\"timestamp\":0,\"topic\":null}");
    }

    @Test
    void createResponseEmpty() {
        @SuppressWarnings("unchecked")
        Future<RecordMetadata> metadata = Mockito.mock(Future.class);

        final String response = KafkaRequestChannel.createResponse(metadata);

        Assertions.assertThat(response).isEqualTo("{}");
    }

    @Test
    void close() {
        ResponseReceiver responseReceiver = Mockito.mock(ResponseReceiver.class);
        KafkaSender sender = Mockito.mock(KafkaSender.class);

        KafkaRequestChannel requestChannel = new KafkaRequestChannel(
                sender,
                responseReceiver
        );

        requestChannel.close();

        Mockito.verify(sender).close();
    }
}