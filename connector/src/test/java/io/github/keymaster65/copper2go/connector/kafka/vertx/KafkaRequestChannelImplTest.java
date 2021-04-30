package io.github.keymaster65.copper2go.connector.kafka.vertx;

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

        requestChannel.request("request", "corrId");

        while ((requestChannel.getSuccessCount() + requestChannel.getFailCount()) < 1) {
            log.info("Wait for response.");
            LockSupport.parkNanos(50 * 1000 * 1000);
        }
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(requestChannel.getSuccessCount()).isEqualTo(1L);
            soft.assertThat(requestChannel.getFailCount()).isEqualTo(0L);
        });
        verify(engine).notify("corrId", "");
    }

    @Test
    void requestFail() {
        Copper2GoKafkaSender sender = Commons.createCopper2GoKafkaSender(Commons.kafka, "");
        Copper2GoEngine engine = Mockito.mock(Copper2GoEngine.class);
        KafkaRequestChannelImpl requestChannel = new KafkaRequestChannelImpl(
                sender,
                engine
        );

        requestChannel.request("request", "corrId");

        while ((requestChannel.getSuccessCount() + requestChannel.getFailCount()) < 1) {
            log.info("Wait for response.");
            LockSupport.parkNanos(50 * 1000 * 1000);
        }
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(requestChannel.getSuccessCount()).isEqualTo(0L);
            soft.assertThat(requestChannel.getFailCount()).isEqualTo(1L);
        });
        verify(engine).notifyError("corrId", "Invalid topics: []");
    }
}