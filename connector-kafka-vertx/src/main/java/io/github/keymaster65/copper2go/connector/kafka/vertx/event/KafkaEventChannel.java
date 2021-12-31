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
package io.github.keymaster65.copper2go.connector.kafka.vertx.event;

import io.github.keymaster65.copper2go.api.connector.EventChannel;
import io.github.keymaster65.copper2go.connector.kafka.vertx.request.KafkaSender;

import java.util.Map;

public class KafkaEventChannel implements EventChannel {

    private final KafkaSender kafkaSender;
    private final KafkaSender copper2GoKafkaErrorSender;

    public KafkaEventChannel(
            final KafkaSender kafkaSender,
            final KafkaSender copper2GoKafkaErrorSender
    ) {
        this.kafkaSender = kafkaSender;
        this.copper2GoKafkaErrorSender = copper2GoKafkaErrorSender;
    }
    @Override
    public void event(final String event, final Map<String, String> attributes) {
        kafkaSender.send(event, attributes);
    }

    @Override
    public void errorEvent(final String event, final Map<String, String> attributes) {
        copper2GoKafkaErrorSender.send(event, attributes);
    }
}
