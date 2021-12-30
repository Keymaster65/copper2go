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

import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.concurrent.locks.LockSupport;

class Commons {

    private Commons() {}
    private static final Logger log = LoggerFactory.getLogger(Commons.class);

    static KafkaContainer kafka;

    static void startContainer() {
        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
        kafka.start();
        while (!kafka.isRunning()) {
            log.info("Wait for kafka running.");
            LockSupport.parkNanos(50L * 1000 * 1000);
        }
        log.info("Kafka server: {} with port {}. Exposed: {}", kafka.getBootstrapServers(), kafka.getFirstMappedPort(), kafka.getExposedPorts());
    }

    static Copper2GoKafkaSenderImpl createCopper2GoKafkaSender(
            final KafkaContainer kafka,
            final String topic
    ) {
        return new Copper2GoKafkaSenderImpl(
                kafka.getHost(),
                kafka.getFirstMappedPort(),
                topic,
                Commons::apply
        );
    }

    static KafkaProducer<String, String> apply(Map<String, String> config) {
        return KafkaProducer.create(Vertx.vertx(), config);
    }
}