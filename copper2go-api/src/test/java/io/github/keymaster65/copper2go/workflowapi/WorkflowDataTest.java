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
package io.github.keymaster65.copper2go.workflowapi;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class WorkflowDataTest {

    private static final String UUID = "UUID";
    private static final String PAYLOAD = "payload";
    private final WorkflowData workflowData = new WorkflowData(WorkflowDataTest.UUID, PAYLOAD);

    @Test
    void getAttribute() {
        Map<String,String> attributes = new HashMap<>();
        attributes.put("a", "A");
        final WorkflowData workflowDataWithAttributes = new WorkflowData(UUID, PAYLOAD, attributes);
        attributes.remove("a");
        Assertions.assertThat(workflowDataWithAttributes.getAttribute("a")).isEqualTo("A");
        Assertions.assertThat(workflowDataWithAttributes.getAttribute("b")).isNull();
    }

    @Test
    void getAttributeNotExistingNull() {
        Assertions.assertThat(workflowData.getAttribute("a")).isNull();
    }

    @Test
    void getAttributesNull() {
        final WorkflowData workflowDataNullAttributes = new WorkflowData(UUID, PAYLOAD, null);

        Assertions.assertThat(workflowDataNullAttributes.getUUID()).isEqualTo(UUID);
        Assertions.assertThat(workflowDataNullAttributes.getPayload()).isEqualTo(PAYLOAD);
        Assertions.assertThat(workflowDataNullAttributes.getAttributes()).isEmpty();
    }

    @Test
    void getUUID() {
        Assertions.assertThat(workflowData.getUUID()).isEqualTo(UUID);
    }

    @Test
    void getPayload() {
        Assertions.assertThat(workflowData.getPayload()).isEqualTo(PAYLOAD);
        workflowData.clearPayload();
        Assertions.assertThat(workflowData.getPayload()).isNull();
    }

    @Test
    void getAttributes() {
        Map<String,String> attributes = new HashMap<>();
        attributes.put("a", "A");
        final WorkflowData workflowDataWithAttributes = new WorkflowData(UUID, PAYLOAD, attributes);
        attributes.remove("a");
        Map<String,String> newAttributes = workflowDataWithAttributes.getAttributes();
        Assertions.assertThat(newAttributes).isNotNull().containsEntry("a","A");
        newAttributes.remove("a");
        Assertions.assertThat(workflowDataWithAttributes.getAttribute("a")).isEqualTo("A");
    }
}