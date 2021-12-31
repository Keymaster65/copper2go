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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KafkaReceiverConfig {

    public final String topic;
    public final String groupId;
    public final String workflowName;
    public final long majorVersion;
    public final long minorVersion;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public KafkaReceiverConfig(
            @JsonProperty(required = true, value = "topic") final String topic,
            @JsonProperty(required = true, value = "groupId") final String groupId,
            @JsonProperty(required = true, value = "workflowName") final String workflowName,
            @JsonProperty(required = true, value = "majorVersion") final long majorVersion,
            @JsonProperty(required = true, value = "minorVersion") final long minorVersion
    ) {
        this.topic = topic;
        this.groupId = groupId;
        this.workflowName = workflowName;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }
}
