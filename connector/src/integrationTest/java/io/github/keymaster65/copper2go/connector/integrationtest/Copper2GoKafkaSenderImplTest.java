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
package io.github.keymaster65.copper2go.connector.integrationtest;

import io.github.keymaster65.copper2go.connector.kafka.vertx.Copper2GoKafkaReceiverImpl;
import io.github.keymaster65.copper2go.connector.kafka.vertx.Copper2GoKafkaSenderImpl;
import io.github.keymaster65.copper2go.connector.kafka.vertx.KafkaReceiverConfig;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.EngineException;
import io.vertx.core.Future;
import io.vertx.kafka.client.producer.RecordMetadata;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.LockSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class Copper2GoKafkaSenderImplTest {

    public static final String REQUEST = "request";
    public static final String TOPIC = "test";

    private static final Logger log = LoggerFactory.getLogger(Copper2GoKafkaSenderImplTest.class);

    @BeforeAll
    static void startContainer() {
        Commons.startContainer();
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
        Copper2GoKafkaSenderImpl copper2GoKafkaSender = Commons.createCopper2GoKafkaSender(Commons.kafka, TOPIC);
        copper2GoKafkaSender.close();
    }

    private Copper2GoKafkaReceiverImpl createCopper2GoKafkaReceiverAndReceive(final Copper2GoEngine engine) {
        Copper2GoKafkaReceiverImpl receiver = new Copper2GoKafkaReceiverImpl(
                Commons.kafka.getHost(),
                Commons.kafka.getFirstMappedPort(),
                new KafkaReceiverConfig(
                        TOPIC,
                        "testGroupId",
                        "Hello",
                        1L,
                        0L)
                ,
                engine
        );
        receiver.start();
        while (receiver.getSuccessCount() + receiver.getFailCount() < 1L) {
            log.info("Wait for receive completion.");
            LockSupport.parkNanos(50L * 1000 * 1000);
        }
        return receiver;
    }

    private Copper2GoKafkaSenderImpl createCopper2GoKafkaSenderAndSend() {
        Copper2GoKafkaSenderImpl sender = Commons.createCopper2GoKafkaSender(Commons.kafka, TOPIC);
        Future<RecordMetadata> future = sender.send(REQUEST);
        while (!future.isComplete()) {
            log.info("Wait for send completion.");
            LockSupport.parkNanos(50L * 1000 * 1000);
        }
        Assertions.assertThat(future.succeeded()).isTrue();
        return sender;
    }
}