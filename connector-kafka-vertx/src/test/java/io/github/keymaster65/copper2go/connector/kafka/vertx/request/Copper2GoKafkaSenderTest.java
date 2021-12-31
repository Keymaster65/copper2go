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
package io.github.keymaster65.copper2go.connector.kafka.vertx.request;

import io.vertx.core.Future;
import io.vertx.kafka.client.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

class Copper2GoKafkaSenderTest {

    public static final String REQUEST = "request";

    @Test
    void send() {
        Copper2GoKafkaSender mockedCopper2GoKafkaSender = Mockito.mock(Copper2GoKafkaSender.class);
        Copper2GoKafkaSender copper2GoKafkaSender = createCopper2GoKafkaSender(mockedCopper2GoKafkaSender);

        copper2GoKafkaSender.send(REQUEST);

        Mockito.verify(mockedCopper2GoKafkaSender).send(REQUEST, null);
    }

    private Copper2GoKafkaSender createCopper2GoKafkaSender(final Copper2GoKafkaSender mockedCopper2GoKafkaSender) {
        return new Copper2GoKafkaSender() {

            @Override
            public Future<RecordMetadata> send(final String request, final Map<String, String> attributes) {
                return mockedCopper2GoKafkaSender.send(request, attributes);
            }

            @Override
            public void close() {
                mockedCopper2GoKafkaSender.close();
            }
        };
    }
}