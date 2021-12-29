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

import io.github.keymaster65.copper2go.application.config.Config;
import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpServer;
import io.github.keymaster65.copper2go.connector.http.vertx.RequestChannelConfigurator;
import io.github.keymaster65.copper2go.connector.http.vertx.RequestHandler;
import io.github.keymaster65.copper2go.connector.http.vertx.VertxHttpServer;
import io.github.keymaster65.copper2go.connector.kafka.vertx.Copper2GoKafkaReceiverImpl;
import io.github.keymaster65.copper2go.connector.kafka.vertx.KafkaConsumerHandler;
import io.github.keymaster65.copper2go.connector.kafka.vertx.KafkaReceiverConfig;
import io.github.keymaster65.copper2go.connector.standardio.StandardInOutEventChannelStoreImpl;
import io.github.keymaster65.copper2go.connector.standardio.StandardInOutException;
import io.github.keymaster65.copper2go.connector.standardio.StandardInOutListener;
import io.github.keymaster65.copper2go.connectorapi.DefaultRequestChannelStore;
import io.github.keymaster65.copper2go.connectorapi.EngineException;
import io.github.keymaster65.copper2go.connectorapi.PayloadReceiver;
import io.github.keymaster65.copper2go.engine.impl.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.impl.ReplyChannelStoreImpl;
import io.github.keymaster65.copper2go.util.Copper2goDependencyInjector;
import org.copperengine.core.DependencyInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

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

        DependencyInjector dependencyInjector = new Copper2goDependencyInjector(
                replyChannelStoreImpl,
                new StandardInOutEventChannelStoreImpl(),
                defaultRequestChannelStore
        );

        Copper2GoHttpServer httpServer = new VertxHttpServer(
                config.httpPort,
                new RequestHandler(copper2GoEngine.getPayloadReceiver()));

        Map<String, Copper2GoKafkaReceiverImpl> kafkaReceiverMap = createKafkaReceiverMap(config.kafkaHost, config.kafkaPort, config.kafkaReceiverConfigs, copper2GoEngine.getPayloadReceiver());
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

    private static Map<String, Copper2GoKafkaReceiverImpl> createKafkaReceiverMap(final String kafkaHost, final int kafkaPort, final Map<String, KafkaReceiverConfig> kafkaReceiverConfigs, final PayloadReceiver copper2GoEngine) {
        Map<String, Copper2GoKafkaReceiverImpl> kafkaReceiverMap = new HashMap<>();
        if (kafkaReceiverConfigs != null) {
            for (Map.Entry<String, KafkaReceiverConfig> entry : kafkaReceiverConfigs.entrySet()) {
                KafkaReceiverConfig config = entry.getValue();
                final var handler = new KafkaConsumerHandler(
                        config.topic,
                        copper2GoEngine,
                        config.workflowName,
                        config.majorVersion,
                        config.minorVersion
                );
                kafkaReceiverMap.put(
                        entry.getKey(),
                        new Copper2GoKafkaReceiverImpl(
                                kafkaHost,
                                kafkaPort,
                                config.topic,
                                config.groupId,
                                handler
                        )
                );
            }
        }
        return kafkaReceiverMap;
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
        final var standardInOutListener = new StandardInOutListener();
        standardInOutListener.listenLocalStream(copper2GoEngine.getPayloadReceiver());
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