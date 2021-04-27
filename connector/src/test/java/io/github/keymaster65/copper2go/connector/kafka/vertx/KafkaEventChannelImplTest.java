package io.github.keymaster65.copper2go.connector.kafka.vertx;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class KafkaEventChannelImplTest {

    public static final String EVENT = "event";

    @Test
    void event() {
        Copper2GoKafkaSender eventChannel = Mockito.mock(Copper2GoKafkaSender.class);
        Copper2GoKafkaSender errorEventChannel = Mockito.mock(Copper2GoKafkaSender.class);
        KafkaEventChannelImpl kafkaEventChannel = new KafkaEventChannelImpl(eventChannel, errorEventChannel);

        kafkaEventChannel.event(EVENT);

        verify(eventChannel).send(EVENT);
        verify(errorEventChannel, times(0)).send(any());
    }

    @Test
    void errorEvent() {
        Copper2GoKafkaSender sender = Mockito.mock(Copper2GoKafkaSender.class);
        Copper2GoKafkaSender errorSender = Mockito.mock(Copper2GoKafkaSender.class);
        KafkaEventChannelImpl kafkaEventChannel = new KafkaEventChannelImpl(sender, errorSender);

        kafkaEventChannel.errorEvent(EVENT);

        verify(errorSender).send(EVENT);
        verify(sender, times(0)).send(any());
    }

}