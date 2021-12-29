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
package io.github.keymaster65.copper2go;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.github.keymaster65.copper2go.application.Application;
import io.github.keymaster65.copper2go.application.config.Config;
import io.github.keymaster65.copper2go.connectorapi.EngineException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

class MainTest {


    @Test
    void constructor() {
        Assertions.assertThatCode(Main::new)
                .doesNotThrowAnyException();
    }

    @Test
    void start() throws EngineException {
        final Application application = Mockito.mock(Application.class);
        Mockito
                .when(application.isStopRequested())
                .thenReturn(false)
                .thenReturn(true);
        final Main main = new Main(application);

        main.start();

        Mockito.verify(application).start();
    }

    @Test
    void startException() throws EngineException {
        final Application application = Mockito.mock(Application.class);
        Mockito.when(application.isStopRequested()).thenReturn(true);
        final EngineException engineException = new EngineException("Test");
        Mockito.doThrow(engineException).when(application).start();
        final Main main = new Main(application);

        Assertions.assertThatCode((main::start)).isEqualTo(engineException);

        Mockito.verify(application).start();
    }

    @Test
    void stopAfterStart() throws EngineException {
        final Application application = Mockito.mock(Application.class);
        Mockito.when(application.isStopRequested()).thenReturn(true);
        final Main main = new Main(application);
        main.start();

        main.stop();

        Mockito.verify(application).stop();
    }

    @Test
    void stopWithoutStart() throws EngineException {
        final Application application = Mockito.mock(Application.class);
        Mockito.when(application.isStopRequested()).thenReturn(true);
        final Main main = new Main(application);

        main.stop();

        Mockito.verify(application, Mockito.never()).stop();
    }

    @Test
    void createDefaultConfig() throws IOException {
        final Config config = Main.createConfig();

        Assertions.assertThat(config.httpPort).isEqualTo(59665);
    }


    @Test
    void createDefaultConfig2() throws IOException {
        final Config config = Main.createConfig();
        ObjectMapper objectMapper = new ObjectMapper();
        final String defaultConfig = objectMapper.writeValueAsString(config);

        final Config configClone = Main.createConfig(defaultConfig);

        Assertions.assertThat(configClone.httpPort).isEqualTo(59665);
    }

    @Test
    void createConfigMismatchedInputException() {
        Assertions.assertThatCode(() -> Main.createConfig("{}"))
                .isInstanceOf(MismatchedInputException.class);
    }
}