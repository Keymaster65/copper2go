package io.github.keymaster65.copper2go.application;

import io.github.keymaster65.copper2go.application.config.Config;
import io.github.keymaster65.copper2go.engine.EngineException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ApplicationTest {

    @Test
    void of() {
        Assertions.assertThatCode(() -> Application.of(Config.of()))
                .doesNotThrowAnyException();
    }

    @Test
    void stop() throws IOException {
        final Application application = Application.of(Config.of());

        Assertions.assertThatCode(application::stop)
                .doesNotThrowAnyException();

        Assertions.assertThat(application.isStopRequested()).isTrue();
    }

    @Test
    void isStopRequestedWithOutStop() throws IOException {
        final Application application = Application.of(Config.of());

        Assertions.assertThat(application.isStopRequested()).isFalse();

    }
}