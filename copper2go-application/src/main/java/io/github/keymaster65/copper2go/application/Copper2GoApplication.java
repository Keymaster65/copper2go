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

import io.github.keymaster65.copper2go.api.connector.DefaultRequestChannelStore;
import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpServer;
import io.github.keymaster65.copper2go.connector.kafka.vertx.receiver.KafkaReceiver;
import io.github.keymaster65.copper2go.connector.standardio.StandardInOutException;
import io.github.keymaster65.copper2go.connector.standardio.receiver.StandardInOutReceiver;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Copper2GoApplication implements Application {
    private static final Logger log = LoggerFactory.getLogger(Copper2GoApplication.class);
    public static final String SYSTEM_STDOUT_EVENT_CHANNEL_NAME = "System.stdout";

    private final Copper2GoEngine copper2GoEngine;
    private final Copper2GoHttpServer httpServer;
    private final DefaultRequestChannelStore defaultRequestChannelStore;
    private final AtomicBoolean stopRequested = new AtomicBoolean(false);
    private final Map<String, KafkaReceiver> kafkaReceiverMap;

    public Copper2GoApplication(
            final Copper2GoEngine copper2GoEngine,
            final Copper2GoHttpServer httpServer,
            final DefaultRequestChannelStore defaultRequestChannelStore,
            final Map<String, KafkaReceiver> kafkaReceiverMap) {
        this.copper2GoEngine = copper2GoEngine;
        this.httpServer = httpServer;
        this.defaultRequestChannelStore = defaultRequestChannelStore;
        this.kafkaReceiverMap = kafkaReceiverMap;
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
        for (Map.Entry<String, KafkaReceiver> entry : kafkaReceiverMap.entrySet()) {
            entry.getValue().start();
        }
    }

    public synchronized void startWithStdInOut() throws ApplicationException, StandardInOutException {
        start();
        final var standardInOutReceiver = new StandardInOutReceiver(new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)));
        standardInOutReceiver.listenLocalStream(copper2GoEngine.payloadReceiver());
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
        for (Map.Entry<String, KafkaReceiver> entry : kafkaReceiverMap.entrySet()) {
            entry.getValue().close();
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