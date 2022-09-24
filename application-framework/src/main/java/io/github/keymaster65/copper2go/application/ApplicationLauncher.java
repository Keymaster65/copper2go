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
package io.github.keymaster65.copper2go.application;

import io.github.keymaster65.copper2go.api.connector.EngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class ApplicationLauncher {
    private static final Logger log = LoggerFactory.getLogger(ApplicationLauncher.class);
    private final Application application;
    private final AtomicBoolean started = new AtomicBoolean(false);

    public ApplicationLauncher(final Application application) {
        this.application = application;
    }

    public void start() throws EngineException {
        try {
            log.info("Start of application.");
            application.start();
            started.set(true);
        } catch (RuntimeException e) {
            log.warn("Exception while starting application. Try to stop application.");
            application.stop();
            throw e;
        } finally {
            while (!application.isStopRequested()) {
                LockSupport.parkNanos(Duration.ofMillis(100).toNanos());
            }
            log.info("End of start.");
        }
    }

    /**
     * @return true, if started application was stopped
     * @throws EngineException in case of exception while stopping the application
     */
    public boolean stop() throws EngineException {
        log.info("Stopping Main.");
        if (started.get()) {
            application.stop();
            started.set(false);
            log.info("Main stopped.");
            return true;
        }
        log.info("Main not started yet.");
        return false;
    }
}
