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

import io.github.keymaster65.copper2go.application.Application;
import io.github.keymaster65.copper2go.application.config.Config;
import io.github.keymaster65.copper2go.engine.EngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static final String ENV_C2G_CONFIG = "C2G_CONFIG";

    private AtomicReference<Application> application = new AtomicReference<>();
    private AtomicBoolean started = new AtomicBoolean(false);

    /**
     *
     * @return true, if started application was stopped
     * @throws EngineException in case of exception while stopping the application
     */
    public boolean stop() throws EngineException {
        log.info("Stopping application.");
        if (started.get()) {
            application.get().stop();
            started.set(false);
            log.info("Application stopped.");
            return true;
        }
        log.info("Application not started yet.");
        return false;
    }

    public static void main(String[] args) throws Exception {
        new Main().start();
    }

    void start() throws EngineException, IOException {
        try {
            log.info("Begin of Main.");
            String configEnv = System.getenv(ENV_C2G_CONFIG);
            if (configEnv != null) {
                log.info("Using config defined in environment variable C2G_CONFIG.");
                application.set(Application.of(Config.of(configEnv)));
            } else {
                log.info("Use default config.");
                application.set(Application.of(Config.of()));
            }
            application.get().start();
            started.set(true);
        } catch (Exception e) {
            log.warn("Exception in application main. Try to stop application.");
            if (application.get() != null) {
                application.get().stop();
            }
            throw e;
        } finally {
            if (application.get() != null) {
                while (!application.get().isStopRequested()) {
                    LockSupport.parkNanos(100L * 1000L * 1000L);
                }
            }
            log.info("End of Main.");
        }
    }
}
