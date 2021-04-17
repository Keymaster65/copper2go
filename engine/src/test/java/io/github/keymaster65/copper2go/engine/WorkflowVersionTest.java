package io.github.keymaster65.copper2go.engine;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class WorkflowVersionTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "/1.2/Hello",
            "localhost/1.2/Hello",
            "http://localhost/1.2/Hello",
            "http://localhost:80/1.2/Hello",
            "http://localhost:80/demoapp/1.2/Hello"
    })
    void getWorkflow(final String uri) throws EngineException {
        WorkflowVersion workflowVersion = WorkflowVersion.of(uri);
        Assertions.assertThat(workflowVersion.name).isEqualTo("Hello");
        Assertions.assertThat(workflowVersion.major).isEqualTo(1L);
        Assertions.assertThat(workflowVersion.minor).isEqualTo(2L);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "a/w",
            "v1.1/w",
            "1/w",
            "w"
    })
    void badUri(final String uri) {
        Assertions.assertThatExceptionOfType(EngineException.class).isThrownBy(() -> WorkflowVersion.of(uri));
    }
}