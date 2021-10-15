package io.github.keymaster65.copper2go.connector.kafka.vertx;

import io.vertx.core.Handler;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class Copper2GoKafkaReceiverImplTest {

    public static final String TOPIC = "topic";

    @Test
    void construct() {
        Assertions.assertThatCode(
                () ->
                {
                    @SuppressWarnings("unchecked") final Handler<KafkaConsumerRecord<String, String>> handler = Mockito.mock(Handler.class);
                    new Copper2GoKafkaReceiverImpl(
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
        final Copper2GoKafkaReceiverImpl kafkaReceiver = new Copper2GoKafkaReceiverImpl(TOPIC, consumer);

        kafkaReceiver.start();

        Mockito.verify(consumer).subscribe(TOPIC);
    }

    @Test
    void close() {
        @SuppressWarnings("unchecked") final KafkaConsumer<String, String> consumer = Mockito.mock(KafkaConsumer.class);
        final Copper2GoKafkaReceiverImpl kafkaReceiver = new Copper2GoKafkaReceiverImpl(TOPIC, consumer);

        kafkaReceiver.close();

        Mockito.verify(consumer).unsubscribe();
        Mockito.verify(consumer).close();
    }
}