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
import io.github.keymaster65.copper2go.connector.kafka.vertx.KafkaConsumerHandler;
import io.github.keymaster65.copper2go.connector.kafka.vertx.KafkaReceiverConfig;

import java.util.HashMap;
import java.util.Map;

public class KafkaReceiverMapFactory {
    private KafkaReceiverMapFactory() {}

    static Map<String, Copper2GoKafkaReceiverImpl> create(
            final String kafkaHost,
            final int kafkaPort,
            final Map<String, KafkaReceiverConfig> kafkaReceiverConfigs,
            final PayloadReceiver payloadReceiver
    ) {
        Map<String, Copper2GoKafkaReceiverImpl> kafkaReceiverMap = new HashMap<>();
        if (kafkaReceiverConfigs != null) {
            for (Map.Entry<String, KafkaReceiverConfig> entry : kafkaReceiverConfigs.entrySet()) {
                KafkaReceiverConfig config = entry.getValue();
                final var handler = new KafkaConsumerHandler(
                        config.topic,
                        payloadReceiver,
                        config.workflowName,
                        config.majorVersion,
                        config.minorVersion
                );
                kafkaReceiverMap.put(
                        entry.getKey(),
                        new Copper2GoKafkaReceiverImpl(
                                kafkaHost,
                                kafkaPort,
                                config.topic,
                                config.groupId,
                                handler
                        )
                );
            }
        }
        return kafkaReceiverMap;
    }
}
