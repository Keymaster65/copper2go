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
package io.github.keymaster65.copper2go.connector.integrationtest.kafka.vertx;

import io.github.keymaster65.copper2go.connector.kafka.vertx.Copper2GoKafkaSender;
import io.github.keymaster65.copper2go.connector.kafka.vertx.KafkaRequestChannelImpl;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.LockSupport;

import static org.mockito.Mockito.verify;

class KafkaRequestChannelImplTest {

    private static final Logger log = LoggerFactory.getLogger(KafkaRequestChannelImplTest.class);
    public static final String CORR_ID = "corrId";

    @BeforeAll
    static void startContainer() {
        Commons.startContainer();
    }

    @Test
    void requestSucess() {
        Copper2GoKafkaSender sender = Commons.createCopper2GoKafkaSender(Commons.kafka, "testTopic");
        Copper2GoEngine engine = Mockito.mock(Copper2GoEngine.class);
        KafkaRequestChannelImpl requestChannel = new KafkaRequestChannelImpl(
                sender,
                engine
        );

        requestChannel.request("request",
                CORR_ID);

        while ((requestChannel.getSuccessCount() + requestChannel.getFailCount()) < 1) {
            log.info("Wait for response.");
            LockSupport.parkNanos(50L * 1000 * 1000);
        }
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(requestChannel.getSuccessCount()).isEqualTo(1L);
            soft.assertThat(requestChannel.getFailCount()).isEqualTo(0L);
        });
        verify(engine).notify(CORR_ID, "");
    }

    @Test
    void requestFail() {
        Copper2GoKafkaSender sender = Commons.createCopper2GoKafkaSender(Commons.kafka, "");
        Copper2GoEngine engine = Mockito.mock(Copper2GoEngine.class);
        KafkaRequestChannelImpl requestChannel = new KafkaRequestChannelImpl(
                sender,
                engine
        );

        requestChannel.request("request", CORR_ID);

        while ((requestChannel.getSuccessCount() + requestChannel.getFailCount()) < 1) {
            log.info("Wait for response.");
            LockSupport.parkNanos(50L * 1000 * 1000);
        }
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(requestChannel.getSuccessCount()).isEqualTo(0L);
            soft.assertThat(requestChannel.getFailCount()).isEqualTo(1L);
        });
        verify(engine).notifyError(CORR_ID, "Invalid topics: []");
    }
}