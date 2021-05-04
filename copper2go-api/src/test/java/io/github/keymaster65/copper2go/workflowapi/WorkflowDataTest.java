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
}