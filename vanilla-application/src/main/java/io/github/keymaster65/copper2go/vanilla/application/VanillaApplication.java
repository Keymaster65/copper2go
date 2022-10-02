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
package io.github.keymaster65.copper2go.vanilla.application;

import io.github.keymaster65.copper2go.api.connector.DefaultRequestChannelStore;
import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.application.Application;
import io.github.keymaster65.copper2go.application.ApplicationException;
import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpServer;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class VanillaApplication implements Application {
    private static final Logger log = LoggerFactory.getLogger(VanillaApplication.class);

    private final Copper2GoEngine copper2GoEngine;
    private final Copper2GoHttpServer httpServer;
    private final DefaultRequestChannelStore defaultRequestChannelStore;
    private final AtomicBoolean stopRequested = new AtomicBoolean(false);

    public VanillaApplication(
            final Copper2GoEngine copper2GoEngine,
            final Copper2GoHttpServer httpServer,
            final DefaultRequestChannelStore defaultRequestChannelStore
    ) {
        this.copper2GoEngine = copper2GoEngine;
        this.httpServer = httpServer;
        this.defaultRequestChannelStore = defaultRequestChannelStore;
    }

    @Override
    public synchronized void start() throws ApplicationException {
        log.info("start application");
        try {
            //noinspection resource
            copper2GoEngine.engineControl().start();
        } catch (EngineException e) {
            throw new ApplicationException("Exception while starting application", e);
        }
        httpServer.start();
    }

    @Override
    public synchronized void stop() throws ApplicationException {
        log.info("stop application");
        stopRequested.set(true);
        try {
            httpServer.stop();
        } catch (Exception e) {
            log.warn("Exception while stopping HTTP server.", e);
        }
        defaultRequestChannelStore.close();
        try {
            //noinspection resource
            copper2GoEngine.engineControl().stop();
        } catch (EngineException e) {
            throw new ApplicationException("Exception while starting application", e);
        }
    }

    @Override
    public synchronized boolean isStopRequested() {
        return this.stopRequested.get();
    }
}