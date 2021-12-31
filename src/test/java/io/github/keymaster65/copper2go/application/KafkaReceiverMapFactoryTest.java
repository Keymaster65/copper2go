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
package io.github.keymaster65.copper2go.application;

import io.github.keymaster65.copper2go.api.connector.PayloadReceiver;
import io.github.keymaster65.copper2go.connector.kafka.vertx.Copper2GoKafkaReceiverImpl;
import io.github.keymaster65.copper2go.connector.kafka.vertx.KafkaReceiverConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Set;

class KafkaReceiverMapFactoryTest {

    @Test
    void create() {
        @SuppressWarnings("unchecked") final Map<String, KafkaReceiverConfig> kafkaReceiverConfigs = Mockito.mock(Map.class);
        @SuppressWarnings("unchecked") final Map.Entry<String, KafkaReceiverConfig> entry = Mockito.mock(Map.Entry.class);
        Mockito.when(kafkaReceiverConfigs.entrySet()).thenReturn(Set.of(entry));
        Mockito.when(entry.getKey()).thenReturn("name");
        final KafkaReceiverConfig kafkaReceiverConfig = new KafkaReceiverConfig(
                "topic",
                "groupId",
                "workflowName",
                1L,
                2L
        );
        Mockito.when(entry.getValue()).thenReturn(kafkaReceiverConfig);

        final Map<String, Copper2GoKafkaReceiverImpl> kafkaReceiverMap = KafkaReceiverMapFactory.create(
                "localhost",
                0,
                kafkaReceiverConfigs,
                Mockito.mock(PayloadReceiver.class)
        );

        Assertions.assertThat(kafkaReceiverMap)
                .hasSize(1)
                .containsKey("name");
    }
}