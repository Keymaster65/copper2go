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
package io.github.keymaster65.copper2go.application;

import io.github.keymaster65.copper2go.application.config.Config;
import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpServer;
import io.github.keymaster65.copper2go.connector.http.vertx.VertxHttpServer;
import io.github.keymaster65.copper2go.connector.standardio.StandardInOutException;
import io.github.keymaster65.copper2go.connector.standardio.StandardInOutListener;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.impl.Copper2GoEngineImpl;
import io.github.keymaster65.copper2go.engine.EngineException;
import io.github.keymaster65.copper2go.util.Copper2goDependencyInjector;
import io.github.keymaster65.copper2go.connector.standardio.StandardInOutEventChannelStoreImpl;
import io.github.keymaster65.copper2go.engine.impl.ReplyChannelStoreImpl;
import io.github.keymaster65.copper2go.connector.http.vertx.RequestChannelStoreImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final Copper2GoEngine copper2GoEngine;
    private final Copper2GoHttpServer httpServer;
    private final org.copperengine.core.DependencyInjector dependencyInjector;
    private boolean stopRequested;

    public static Application of(final Config config) {
        ReplyChannelStoreImpl replyChannelStore = new ReplyChannelStoreImpl();

        Copper2GoEngine copper2GoEngine = new Copper2GoEngineImpl(
                config.maxTickets,
                config.workflowRepositoryConfig,
                replyChannelStore);
        org.copperengine.core.DependencyInjector dependencyInjector = new Copper2goDependencyInjector(
                replyChannelStore,
                new StandardInOutEventChannelStoreImpl(),
                new RequestChannelStoreImpl(config.httpRequestChannelConfigs, copper2GoEngine));
        Copper2GoHttpServer httpServer = new VertxHttpServer(
                config.httpPort,
                copper2GoEngine);
        return new Application(
                copper2GoEngine,
                dependencyInjector,
                httpServer
        );
    }

    public Application(
            final Copper2GoEngine copper2GoEngine,
            final org.copperengine.core.DependencyInjector dependencyInjector,
            final Copper2GoHttpServer httpServer
    ) {
        this.copper2GoEngine = copper2GoEngine;
        this.dependencyInjector = dependencyInjector;
        this.httpServer = httpServer;
    }

    public synchronized void start() throws EngineException {
        log.info("start application");
        copper2GoEngine.start(dependencyInjector);
        httpServer.start();
    }

    public synchronized void startWithStdInOut() throws EngineException, StandardInOutException {
        start();
        final StandardInOutListener standardInOutListener = new StandardInOutListener();
        standardInOutListener.listenLocalStream(copper2GoEngine);
    }

    public synchronized void stop() throws EngineException {
        log.info("stop application");
        stopRequested = true;
        try {
            httpServer.stop();
        } catch (Exception e) {
            log.warn("Exception while stopping HTTP server.", e);
        }
        copper2GoEngine.stop();
    }

    public synchronized boolean isStopRequested() {
        return this.stopRequested;
    }
}