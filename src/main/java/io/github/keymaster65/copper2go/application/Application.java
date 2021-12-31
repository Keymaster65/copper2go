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

import io.github.keymaster65.copper2go.api.connector.DefaultEventChannelStore;
import io.github.keymaster65.copper2go.api.connector.DefaultRequestChannelStore;
import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.api.util.Copper2goDependencyInjector;
import io.github.keymaster65.copper2go.application.config.Config;
import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpServer;
import io.github.keymaster65.copper2go.connector.http.vertx.RequestChannelConfigurator;
import io.github.keymaster65.copper2go.connector.http.vertx.RequestHandler;
import io.github.keymaster65.copper2go.connector.http.vertx.VertxHttpServer;
import io.github.keymaster65.copper2go.connector.kafka.vertx.Copper2GoKafkaReceiverImpl;
import io.github.keymaster65.copper2go.connector.standardio.StandardInOutException;
import io.github.keymaster65.copper2go.connector.standardio.event.StandardOutEventChannel;
import io.github.keymaster65.copper2go.connector.standardio.receiver.StandardInOutReceiver;
import io.github.keymaster65.copper2go.engine.impl.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.impl.ReplyChannelStoreImpl;
import org.copperengine.core.DependencyInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    public static final String SYSTEM_STDOUT_EVENT_CHANNEL_NAME = "System.stdout";

    private final Copper2GoEngine copper2GoEngine;
    private final Copper2GoHttpServer httpServer;
    private final DefaultRequestChannelStore defaultRequestChannelStore;
    private final DependencyInjector dependencyInjector;
    private final AtomicBoolean stopRequested = new AtomicBoolean(false);
    private final Map<String, Copper2GoKafkaReceiverImpl> kafkaReceiverMap;

    public static Application of(final Config config) {

        var replyChannelStoreImpl = new ReplyChannelStoreImpl();
        var copper2GoEngine = createCopper2GoEngine(config, replyChannelStoreImpl);

        final DefaultRequestChannelStore defaultRequestChannelStore = new DefaultRequestChannelStore();

        RequestChannelConfigurator.putHttpRequestChannels(
                config.httpRequestChannelConfigs,
                copper2GoEngine.getResponseReceiver(),
                defaultRequestChannelStore
        );

        io.github.keymaster65.copper2go.connector.kafka.vertx.RequestChannelConfigurator.addKafkaRequestChannels(
                config.kafkaHost,
                config.kafkaPort,
                config.kafkaRequestChannelConfigs,
                copper2GoEngine.getResponseReceiver(),
                defaultRequestChannelStore
        );

        final DefaultEventChannelStore defaultEventChannelStore = new DefaultEventChannelStore();
        defaultEventChannelStore.put(SYSTEM_STDOUT_EVENT_CHANNEL_NAME, new StandardOutEventChannel(System.out, System.err)); // NOSONAR

        DependencyInjector dependencyInjector = new Copper2goDependencyInjector(
                replyChannelStoreImpl,
                defaultEventChannelStore,
                defaultRequestChannelStore
        );

        Copper2GoHttpServer httpServer = new VertxHttpServer(
                config.httpPort,
                new RequestHandler(copper2GoEngine.getPayloadReceiver()));

        Map<String, Copper2GoKafkaReceiverImpl> kafkaReceiverMap = KafkaReceiverMapFactory.create(config.kafkaHost, config.kafkaPort, config.kafkaReceiverConfigs, copper2GoEngine.getPayloadReceiver());
        return new Application(
                copper2GoEngine,
                dependencyInjector,
                httpServer,
                defaultRequestChannelStore,
                kafkaReceiverMap
        );
    }

    public static Copper2GoEngine createCopper2GoEngine(final Config config, final ReplyChannelStoreImpl replyChannelStoreImpl) {
        return new Copper2GoEngine(
                config.maxTickets,
                config.workflowRepositoryConfig,
                replyChannelStoreImpl);
    }

    public Application(
            final Copper2GoEngine copper2GoEngine,
            final DependencyInjector dependencyInjector,
            final Copper2GoHttpServer httpServer,
            final DefaultRequestChannelStore defaultRequestChannelStore,
            final Map<String, Copper2GoKafkaReceiverImpl> kafkaReceiverMap) {
        this.copper2GoEngine = copper2GoEngine;
        this.dependencyInjector = dependencyInjector;
        this.httpServer = httpServer;
        this.defaultRequestChannelStore = defaultRequestChannelStore;
        this.kafkaReceiverMap = kafkaReceiverMap;
    }

    public synchronized void start() throws EngineException {
        log.info("start application");
        copper2GoEngine.getEngineControl().start(dependencyInjector);
        httpServer.start();
        for (Map.Entry<String, Copper2GoKafkaReceiverImpl> entry : kafkaReceiverMap.entrySet()) {
            entry.getValue().start();
        }
    }

    public synchronized void startWithStdInOut() throws EngineException, StandardInOutException {
        start();
        final var standardInOutReceiver = new StandardInOutReceiver(new BufferedReader(new InputStreamReader(System.in)));
        standardInOutReceiver.listenLocalStream(copper2GoEngine.getPayloadReceiver());
    }

    public synchronized void stop() throws EngineException {
        log.info("stop application");
        stopRequested.set(true);
        copper2GoEngine.getEngineControl().stop();
        try {
            httpServer.stop();
        } catch (Exception e) {
            log.warn("Exception while stopping HTTP server.", e);
        }
        for (Map.Entry<String, Copper2GoKafkaReceiverImpl> entry : kafkaReceiverMap.entrySet()) {
            entry.getValue().close();
        }

        defaultRequestChannelStore.close();
    }

    public synchronized boolean isStopRequested() {
        return this.stopRequested.get();
    }
}