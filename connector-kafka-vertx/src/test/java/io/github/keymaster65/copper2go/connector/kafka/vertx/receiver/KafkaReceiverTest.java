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
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class KafkaReceiverTest {

    public static final String TOPIC = "topic";

    @Test
    void construct() {
        Assertions.assertThatCode(
                () ->
                {
                    @SuppressWarnings("unchecked") final Handler<KafkaConsumerRecord<String, String>> handler = Mockito.mock(Handler.class);
                    new KafkaReceiver(
                            "localhost",
                            0,
                            TOPIC,
                            "groupId",
                            handler
                    );
                }).doesNotThrowAnyException();
    }

    @Test
    void start() {
        @SuppressWarnings("unchecked") final KafkaConsumer<String, String> consumer = Mockito.mock(KafkaConsumer.class);
        final KafkaReceiver kafkaReceiver = new KafkaReceiver(TOPIC, consumer);

        kafkaReceiver.start();

        Mockito.verify(consumer).subscribe(TOPIC);
    }

    @Test
    void close() {
        @SuppressWarnings("unchecked") final KafkaConsumer<String, String> consumer = Mockito.mock(KafkaConsumer.class);
        final KafkaReceiver kafkaReceiver = new KafkaReceiver(TOPIC, consumer);

        kafkaReceiver.close();

        Mockito.verify(consumer).unsubscribe();
        Mockito.verify(consumer).close();
    }
}