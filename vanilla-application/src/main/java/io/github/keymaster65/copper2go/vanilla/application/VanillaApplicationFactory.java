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

import io.github.keymaster65.copper2go.api.connector.DefaultEventChannelStore;
import io.github.keymaster65.copper2go.api.connector.DefaultRequestChannelStore;
import io.github.keymaster65.copper2go.application.Application;
import io.github.keymaster65.copper2go.application.ApplicationFactory;
import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpServer;
import io.github.keymaster65.copper2go.connector.http.vertx.receiver.RequestHandler;
import io.github.keymaster65.copper2go.connector.http.vertx.receiver.VertxHttpServer;
import io.github.keymaster65.copper2go.connector.http.vertx.request.HttpRequestChannelConfig;
import io.github.keymaster65.copper2go.connector.http.vertx.request.RequestChannelConfigurator;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.ReplyChannelStoreImpl;
import io.github.keymaster65.copper2go.engine.vanilla.impl.Copper2GoEngineFactory;
import io.github.keymaster65.copper2go.engine.vanilla.impl.ExpectedResponsesStore;
import io.github.keymaster65.copper2go.engine.vanilla.impl.FutureStore;
import io.github.keymaster65.copper2go.engine.vanilla.workflowapi.Workflow;
import io.github.keymaster65.copper2go.vanilla.workflow.WorkflowFactoryFactoryImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VanillaApplicationFactory implements ApplicationFactory {
    @Override
    public Application create() {

        var replyChannelStoreImpl = new ReplyChannelStoreImpl();
        final DefaultEventChannelStore defaultEventChannelStore = new DefaultEventChannelStore();
        final DefaultRequestChannelStore defaultRequestChannelStore = new DefaultRequestChannelStore();


        final Copper2GoEngine copper2GoEngine = createCopper2GoVanillyEngine(
                replyChannelStoreImpl,
                defaultEventChannelStore,
                defaultRequestChannelStore
        );


        RequestChannelConfigurator.putHttpRequestChannels(
                createHttpRequestChannelConfigs(),
                copper2GoEngine.responseReceiver(),
                defaultRequestChannelStore
        );

        Copper2GoHttpServer httpServer = new VertxHttpServer(
                59665,
                new RequestHandler(copper2GoEngine.payloadReceiver()));

        return new VanillaApplication(
                copper2GoEngine,
                httpServer,
                defaultRequestChannelStore
        );
    }

    private Map<String, HttpRequestChannelConfig> createHttpRequestChannelConfigs() {
        final HttpRequestChannelConfig pricingChannel = new HttpRequestChannelConfig(
                "localhost",
                59665,
                "/copper2go/3/api/twoway/1.0/Pricing",
                "GET"
        );

        return Map.of("Pricing.centPerMinute", pricingChannel);
    }

    private static Copper2GoEngine createCopper2GoVanillyEngine(
            final ReplyChannelStoreImpl replyChannelStoreImpl,
            final DefaultEventChannelStore defaultEventChannelStore,
            final DefaultRequestChannelStore defaultRequestChannelStore

    ) {
        return Copper2GoEngineFactory.create(
                replyChannelStoreImpl,
                defaultRequestChannelStore,
                defaultEventChannelStore,
                new FutureStore<>(Workflow.class),
                new ExpectedResponsesStore(new ConcurrentHashMap<>()),
                new WorkflowFactoryFactoryImpl()
        );
    }
}
