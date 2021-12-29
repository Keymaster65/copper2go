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
import io.github.keymaster65.copper2go.connectorapi.EngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static final String ENV_C2G_CONFIG = "C2G_CONFIG";

    private final AtomicReference<Application> applicationReference = new AtomicReference<>();
    private final AtomicBoolean started = new AtomicBoolean(false);

    public Main() throws IOException {
        this(Application.of(createConfig()));
    }

    public Main(final Application theApplication) {
        applicationReference.set(theApplication);
    }

    // tested in system or integrationtest
    public static void main(String[] args) throws Exception {
        new Main().start();
    }

    static Config createConfig() throws IOException {
        return createConfig(System.getenv(ENV_C2G_CONFIG));
    }

    static Config createConfig(final String config) throws IOException {
        if (config != null) {
            log.info("Using config defined in environment variable {}.", config);
            return Config.of(config);
        }
        log.info("Use default config.");
        return Config.of();
    }

    void start() throws EngineException {
        try {
            log.info("Start of Main.");
            applicationReference.get().start();
            started.set(true);
        } catch (Exception e) {
            log.warn("Exception in application main. Try to stop application.");
            if (applicationReference.get() != null) {
                applicationReference.get().stop();
            }
            throw e;
        } finally {
            if (applicationReference.get() != null) {
                while (!applicationReference.get().isStopRequested()) {
                    log.debug("Wait for stop request.");
                    LockSupport.parkNanos(100L * 1000L * 1000L);
                }
            }
            log.info("End of Main.");
        }
    }

    /**
     * @return true, if started application was stopped
     * @throws EngineException in case of exception while stopping the application
     */
    boolean stop() throws EngineException {
        log.info("Stopping Main.");
        if (started.get()) {
            applicationReference.get().stop();
            started.set(false);
            log.info("Main stopped.");
            return true;
        }
        log.info("Main not started yet.");
        return false;
    }
}
