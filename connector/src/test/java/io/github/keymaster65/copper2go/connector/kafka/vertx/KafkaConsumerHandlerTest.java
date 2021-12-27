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

import io.github.keymaster65.copper2go.engine.Engine;
import io.github.keymaster65.copper2go.engine.EngineException;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import io.vertx.kafka.client.producer.impl.KafkaHeaderImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

class KafkaConsumerHandlerTest {

    public static final String WORKFLOW_NAME = "workflowName";
    public static final long MAJOR = 1L;
    public static final long MINOR = 2L;

    @Test
    void handleSucess() throws EngineException {

        final Engine copper2GoEngine = Mockito.mock(Engine.class);

        final KafkaConsumerHandler kafkaConsumerHandler = createKafkaConsumerHandler(copper2GoEngine);

        @SuppressWarnings("unchecked")
        final KafkaConsumerRecord<String, String>  event = Mockito.mock(KafkaConsumerRecord.class);

        kafkaConsumerHandler.handle(event);

        Mockito.verify(copper2GoEngine).callWorkflow(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.eq(WORKFLOW_NAME),
                Mockito.eq(MAJOR),
                Mockito.eq(MINOR)
        );
        Assertions.assertThat(kafkaConsumerHandler.getSuccessCount()).isOne();
        Assertions.assertThat(kafkaConsumerHandler.getFailCount()).isZero();
    }

    @Test
    void handleFail() throws EngineException {

        @SuppressWarnings("unchecked")
        final KafkaConsumerRecord<String, String>  event = Mockito.mock(KafkaConsumerRecord.class);
        final Engine copper2GoEngine = Mockito.mock(Engine.class);
        final KafkaConsumerHandler kafkaConsumerHandler = createKafkaConsumerHandler(copper2GoEngine);
        Mockito.doThrow(new RuntimeException("Test")).when(copper2GoEngine).callWorkflow(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.eq(WORKFLOW_NAME),
                Mockito.eq(MAJOR),
                Mockito.eq(MINOR)
        );

        kafkaConsumerHandler.handle(event);

        Mockito.verify(copper2GoEngine).callWorkflow(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.eq(WORKFLOW_NAME),
                Mockito.eq(MAJOR),
                Mockito.eq(MINOR)
        );
        Assertions.assertThat(kafkaConsumerHandler.getSuccessCount()).isZero();
        Assertions.assertThat(kafkaConsumerHandler.getFailCount()).isOne();
    }

    private KafkaConsumerHandler createKafkaConsumerHandler(final Engine copper2GoEngine) {
        return new KafkaConsumerHandler(
                "topic",
                copper2GoEngine,
                WORKFLOW_NAME,
                MAJOR,
                MINOR
        );
    }

    @Test
    void createAttributes() {
        final String key = "key";
        final String value = "value";
        final KafkaHeaderImpl kafkaHeader = new KafkaHeaderImpl(key, value);
        final KafkaHeaderImpl kafkaHeader2 = new KafkaHeaderImpl(key, value + "2");

        final Map<String, String> attributes = KafkaConsumerHandler.createAttributes(List.of(kafkaHeader, kafkaHeader2));

        Assertions.assertThat(attributes.size()).isOne();
        Assertions.assertThat(attributes.get(key)).startsWith(value);
    }


    @Test
    void createAttributesEmpty() {
        Assertions.assertThat(KafkaConsumerHandler.createAttributes(List.of())).isEmpty();
    }

    @Test
    void createAttributesNull() {
        Assertions.assertThat(KafkaConsumerHandler.createAttributes(null)).isEmpty();
    }
}