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

import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.application.Application;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.locks.LockSupport;

class ApplicationLauncherTest {

    private static final Logger log = LoggerFactory.getLogger(ApplicationLauncherTest.class);

    @Test
    void constructor() {
        Assertions
                .assertThatCode(() -> new ApplicationLauncher(Mockito.mock(Application.class)))
                .doesNotThrowAnyException();
    }

    @Test
    void start() throws EngineException {
        final Application application = Mockito.mock(Application.class);
        Mockito
                .when(application.isStopRequested())
                .thenReturn(false)
                .thenReturn(true);
        final ApplicationLauncher applicationLauncher = new ApplicationLauncher(application);

        applicationLauncher.start();

        Mockito.verify(application).start();
    }

    @Test
    void startEngineException() throws EngineException {
        final Application application = Mockito.mock(Application.class);
        Mockito.when(application.isStopRequested()).thenReturn(true);
        final EngineException engineException = new EngineException("Test");
        Mockito.doThrow(engineException).when(application).start();
        final ApplicationLauncher applicationLauncher = new ApplicationLauncher(application);

        Assertions.assertThatCode(applicationLauncher::start).isEqualTo(engineException);

        Mockito.verify(application).start();
    }

    @Test
    void stopAfterStart() throws EngineException {
        final Application application = Mockito.mock(Application.class);
        Mockito.when(application.isStopRequested()).thenReturn(true);
        final ApplicationLauncher applicationLauncher = new ApplicationLauncher(application);
        applicationLauncher.start();

        applicationLauncher.stop();

        Mockito.verify(application).stop();
    }

    @Test
    void stopWithoutStart() throws EngineException {
        final Application application = Mockito.mock(Application.class);
        Mockito.when(application.isStopRequested()).thenReturn(true);
        final ApplicationLauncher applicationLauncher = new ApplicationLauncher(application);

        applicationLauncher.stop();

        Mockito.verify(application, Mockito.never()).stop();
    }

    @Test
    void mainStartStop() throws EngineException {
        final Application application = Mockito.mock(Application.class);
        Mockito.when(application.isStopRequested()).thenReturn(true);
        final ApplicationLauncher applicationLauncher = new ApplicationLauncher(application);
        new Thread(() -> ApplicationLauncherTest.stopDelayed(applicationLauncher)).start();

        applicationLauncher.start();
    }

    private static void stopDelayed(
            final ApplicationLauncher applicationLauncher
    ) {
        try {
            while (!applicationLauncher.stop()) {
                log.debug("Wait for stoped appalication");
                LockSupport.parkNanos(Duration.ofMillis(500).toNanos());
            }
        } catch (RuntimeException | EngineException e) {
            log.error("Ignore exception.", e);
        }
    }
}