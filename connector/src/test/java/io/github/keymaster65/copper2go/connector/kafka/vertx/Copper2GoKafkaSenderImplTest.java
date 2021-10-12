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
import io.vertx.kafka.client.producer.KafkaHeader;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.RecordMetadata;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class Copper2GoKafkaSenderImplTest {

    @Test
    void send() {
        @SuppressWarnings("unchecked")
        Function<Map<String, String>, KafkaProducer<String, String>> producerFactory =
                        Mockito.mock(Function.class);

        @SuppressWarnings("unchecked")
        KafkaProducer<String, String> producer =
                        Mockito.mock(KafkaProducer.class);

        @SuppressWarnings("unchecked")
        Future<RecordMetadata> sendFuture = Mockito.mock(Future.class);

        Mockito.when(producer.send(Mockito.any())).thenReturn(sendFuture);
        Mockito.when(producerFactory.apply(Mockito.any())).thenReturn(producer);

        final Copper2GoKafkaSender copper2GoKafkaSender = new Copper2GoKafkaSenderImpl(
                "host",
                0,
                "topic",
                producerFactory
        );
        copper2GoKafkaSender.send(
                "request",
                Map.of()
        );
        copper2GoKafkaSender.close();

        Mockito.verify(producerFactory).apply(Mockito.any());
        Mockito.verify(producer).send(Mockito.any());
        Mockito.verify(producer).close();

    }

    @Test
    void createHeader() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("a", "A");
        final List<KafkaHeader> header = Copper2GoKafkaSenderImpl.createHeader(attributes);
        Assertions.assertThat(header).hasSize(1);
        Assertions.assertThat(header.get(0).key()).isEqualTo("a");
        Assertions.assertThat(header.get(0).value().toString(StandardCharsets.UTF_8)).isEqualTo("A");
    }

    @Test
    void createHeaderEmpty() {
        Map<String, String> attributes = new HashMap<>();
        Assertions.assertThat(Copper2GoKafkaSenderImpl.createHeader(attributes)).isEmpty();
    }

    @Test
    void createHeaderNull() {
        Assertions.assertThat(Copper2GoKafkaSenderImpl.createHeader(null)).isEmpty();
    }
}