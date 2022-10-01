package io.github.keymaster65.copper2go.engine.sync.engineapi;


import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

class EngineExceptionTest {

    @Example
    void create() {
        final Exception cause = new Exception("Test cause");
        final String message = "Test";

        final EngineException engineException = new EngineException(message, cause);

        Assertions
                .assertThat(engineException)
                .hasMessage(message)
                .hasCauseReference(cause);
    }

}