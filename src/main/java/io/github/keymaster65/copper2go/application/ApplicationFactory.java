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
import io.github.keymaster65.copper2go.api.util.Copper2goDependencyInjector;
import io.github.keymaster65.copper2go.application.config.Config;
import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpServer;
import io.github.keymaster65.copper2go.connector.http.vertx.request.RequestChannelConfigurator;
import io.github.keymaster65.copper2go.connector.http.vertx.receiver.RequestHandler;
import io.github.keymaster65.copper2go.connector.http.vertx.receiver.VertxHttpServer;
import io.github.keymaster65.copper2go.connector.kafka.vertx.receiver.Copper2GoKafkaReceiverImpl;
import io.github.keymaster65.copper2go.connector.standardio.event.StandardOutEventChannel;
import io.github.keymaster65.copper2go.engine.impl.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.impl.ReplyChannelStoreImpl;
import org.copperengine.core.DependencyInjector;

import java.util.Map;

public class ApplicationFactory {

    private ApplicationFactory() {}

    public static Application of(final Config config) {

        var replyChannelStoreImpl = new ReplyChannelStoreImpl();
        var copper2GoEngine = createCopper2GoEngine(config, replyChannelStoreImpl);

        final DefaultRequestChannelStore defaultRequestChannelStore = new DefaultRequestChannelStore();

        RequestChannelConfigurator.putHttpRequestChannels(
                config.httpRequestChannelConfigs,
                copper2GoEngine.getResponseReceiver(),
                defaultRequestChannelStore
        );

        io.github.keymaster65.copper2go.connector.kafka.vertx.request.RequestChannelConfigurator.putKafkaRequestChannels(
                config.kafkaHost,
                config.kafkaPort,
                config.kafkaRequestChannelConfigs,
                copper2GoEngine.getResponseReceiver(),
                defaultRequestChannelStore
        );

        final DefaultEventChannelStore defaultEventChannelStore = new DefaultEventChannelStore();
        defaultEventChannelStore.put(Application.SYSTEM_STDOUT_EVENT_CHANNEL_NAME, new StandardOutEventChannel(System.out, System.err)); // NOSONAR

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
}
