/*
 * Copyright 2019 Wolf Sluyterman van Langeweyde
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
package de.wolfsvl.copper2go.application;

import de.wolfsvl.copper2go.connector.Copper2GoHttpServer;
import de.wolfsvl.copper2go.connector.standardio.StandardInOutException;
import de.wolfsvl.copper2go.connector.standardio.StandardInOutListener;
import de.wolfsvl.copper2go.connector.vertx.VertxHttpServer;
import de.wolfsvl.copper2go.engine.Copper2GoEngine;
import de.wolfsvl.copper2go.engine.Copper2GoEngineImpl;
import de.wolfsvl.copper2go.engine.EngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final Copper2GoEngine copper2GoEngine;
    private final Copper2GoHttpServer httpServer;

    public static void main(String[] args) throws Exception {
        Application application = null;
        try {
            log.info("Begin of Main.");
            application = new Application(args);
            application.start();
        } catch (Exception e) {
            log.error("Exception in application main.", e);
        } finally {
            if (application != null) {
                application.stop();
            }
            log.info("End of Main.");
        }
    }

    public Application(final String[] args) {
        copper2GoEngine = new Copper2GoEngineImpl(args);
        httpServer = new VertxHttpServer(8080, copper2GoEngine);
    }

    public synchronized void start() throws EngineException, StandardInOutException {
        log.info("start application");
        copper2GoEngine.start();
        httpServer.start();
        final StandardInOutListener standardInOutListener = new StandardInOutListener();
        standardInOutListener.listenLocalStream(copper2GoEngine);
    }

    public synchronized void stop() throws EngineException {
        log.info("stop application");
        try {
            httpServer.stop();
        } catch (Exception e) {
            log.warn("Exception while stopping HTTP server.", e);
        }
        copper2GoEngine.stop();
    }
}