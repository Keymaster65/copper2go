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

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class KafkaEventChannelImplTest {

    public static final String EVENT = "event";

    @Test
    void event() {
        Copper2GoKafkaSender sender = Mockito.mock(Copper2GoKafkaSender.class);
        Copper2GoKafkaSender errorSender = Mockito.mock(Copper2GoKafkaSender.class);
        KafkaEventChannelImpl kafkaEventChannel = new KafkaEventChannelImpl(sender, errorSender);

        kafkaEventChannel.event(EVENT);

        verify(sender).send(EVENT);
        verify(errorSender, times(0)).send(any());
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