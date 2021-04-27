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

class Copper2GoKafkaSenderImplTest {

    public static final String REQUEST = "request";
    public static final String TOPIC = "test";
    private static KafkaContainer kafka;

    private static final Logger log = LoggerFactory.getLogger(Copper2GoKafkaSenderImplTest.class);

    @BeforeAll
    static void startContainer() {
        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
        kafka.start();
        while (!kafka.isRunning()) {
            log.info("Wait for kafka running.");
            LockSupport.parkNanos(50 * 1000 * 1000);
        }
        log.info("Kafka server: {} with port {}. Exposed: {}", kafka.getBootstrapServers(), kafka.getFirstMappedPort(), kafka.getExposedPorts());
    }

    @Test
    void sendAndReceive() throws EngineException {

        Copper2GoKafkaSenderImpl sender = createCopper2GoKafkaSenderAndSend();
        sender.close();

        Copper2GoEngine engine = mock(Copper2GoEngine.class);
        Copper2GoKafkaReceiverImpl receiver = createCopper2GoKafkaReceiverAndReceive(engine);
        receiver.close();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(receiver.getSuccessCount()).isEqualTo(1L);
            soft.assertThat(receiver.getFailCount()).isEqualTo(0L);
        });
        verify(engine).callWorkflow(eq(REQUEST), any(), anyString(), eq(1L), eq(0L));
    }

    @Test
    void sendAndReceiveFail() throws EngineException {

        Copper2GoKafkaSenderImpl sender = createCopper2GoKafkaSenderAndSend();
        sender.close();

        Copper2GoEngine engine = mock(Copper2GoEngine.class);
        Mockito
                .doThrow(new EngineException("Simulated exception."))
                .when(engine)
                .callWorkflow(eq(REQUEST), any(), anyString(), eq(1L), eq(0L));

        Copper2GoKafkaReceiverImpl receiver = createCopper2GoKafkaReceiverAndReceive(engine);
        receiver.close();


        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(receiver.getSuccessCount()).isEqualTo(0L);
            soft.assertThat(receiver.getFailCount()).isEqualTo(1L);
        });
        verify(engine).callWorkflow(eq(REQUEST), any(), anyString(), eq(1L), eq(0L));
    }

    @Test
    void close() {
        Copper2GoKafkaSenderImpl copper2GoKafkaSender = createCopper2GoKafkaSender();
        copper2GoKafkaSender.close();
    }

    private Copper2GoKafkaReceiverImpl createCopper2GoKafkaReceiverAndReceive(final Copper2GoEngine engine) {
        Copper2GoKafkaReceiverImpl receiver = new Copper2GoKafkaReceiverImpl(
                kafka.getHost(),
                kafka.getFirstMappedPort(),
                TOPIC,
                "testGroupId",
                engine
        );
        receiver.start();
        while (receiver.getSuccessCount() + receiver.getFailCount() < 1L) {
            log.info("Wait for receive completion.");
            LockSupport.parkNanos(50 * 1000 * 1000);
        }
        return receiver;
    }

    private Copper2GoKafkaSenderImpl createCopper2GoKafkaSenderAndSend() {
        Copper2GoKafkaSenderImpl sender = createCopper2GoKafkaSender();
        Future<RecordMetadata> future = sender.send(REQUEST);
        while (!future.isComplete()) {
            log.info("Wait for send completion.");
            LockSupport.parkNanos(50 * 1000 * 1000);
        }
        Assertions.assertThat(future.succeeded()).isTrue();
        return sender;
    }

    private Copper2GoKafkaSenderImpl createCopper2GoKafkaSender() {
        return new Copper2GoKafkaSenderImpl(
                kafka.getHost(),
                kafka.getFirstMappedPort(),
                TOPIC,
                Copper2GoKafkaSenderImplTest::apply
        );
    }

    private static KafkaProducer<String, String> apply(Map<String, String> config) {
        return KafkaProducer.create(Vertx.vertx(), config);
    }
}