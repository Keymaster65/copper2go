package io.github.keymaster65.copper2go.connector.kafka.vertx;

import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.EngineException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.RecordMetadata;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.concurrent.locks.LockSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class Commons {

    private Commons() {}
    private static final Logger log = LoggerFactory.getLogger(Commons.class);

    static KafkaContainer kafka;

    static void startContainer() {
        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
        kafka.start();
        while (!kafka.isRunning()) {
            log.info("Wait for kafka running.");
            LockSupport.parkNanos(50 * 1000 * 1000);
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