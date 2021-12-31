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
package io.github.keymaster65.copper2go.connector.kafka.vertx.receiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

class KafkaReceiverConfigTest {

    public static final String TOPIC = "/topic";
    public static final String GROUP_ID = "/groupId";
    public static final String WORKFLOW_NAME = "/workflowName";
    public static final long MAJOR_VERSION = 1L;
    public static final long MINOR_VERSION = 2L;

    public static final String WORKFLOW_REPOSITORY_CONFIG = """
            {
                "topic": "%s",
                "groupId": "%s",
                "workflowName": "%s",
                "majorVersion": "%d",
                "minorVersion": "%d"
            }
                        """;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Example
    void constructor() throws JsonProcessingException {


        final KafkaReceiverConfig kafkaReceiverConfig = objectMapper.readValue(
                String.format(WORKFLOW_REPOSITORY_CONFIG, TOPIC, GROUP_ID, WORKFLOW_NAME, MAJOR_VERSION, MINOR_VERSION),
                KafkaReceiverConfig.class
        );

        Assertions.assertThat(kafkaReceiverConfig.topic).isEqualTo(TOPIC);
        Assertions.assertThat(kafkaReceiverConfig.groupId).isEqualTo(GROUP_ID);
        Assertions.assertThat(kafkaReceiverConfig.workflowName).isEqualTo(WORKFLOW_NAME);
        Assertions.assertThat(kafkaReceiverConfig.majorVersion).isEqualTo(MAJOR_VERSION);
        Assertions.assertThat(kafkaReceiverConfig.minorVersion).isEqualTo(MINOR_VERSION);
    }

    @Example
    void constructorException() {
        Assertions.assertThatCode(() ->
                        objectMapper.readValue("{}", KafkaReceiverConfig.class)
                )
                .isNotInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Missing required creator property 'topic'");
    }

}