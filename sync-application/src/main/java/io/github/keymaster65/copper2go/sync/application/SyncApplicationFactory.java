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
package io.github.keymaster65.copper2go.sync.application;

import com.sun.net.httpserver.HttpServer; // NOSONAR
import io.github.keymaster65.copper2go.application.Application;
import io.github.keymaster65.copper2go.application.ApplicationFactory;
import io.github.keymaster65.copper2go.engine.sync.impl.SyncEngineImpl;
import io.github.keymaster65.copper2go.sync.application.workflow.WorkflowFactoryImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncApplicationFactory implements ApplicationFactory {

    private final HttpServer httpServer;
    private final SyncEngineImpl syncEngine;
    private final ExecutorService executorService;
    private final SyncHandler syncHandler;
    private final URI uri;

    public SyncApplicationFactory() throws IOException, URISyntaxException {
        this(
                HttpServer.create(new InetSocketAddress(59665), 0),
                new SyncEngineImpl(),
                Executors.newVirtualThreadPerTaskExecutor(),
                new URI("http://localhost:59665/Pricing") // NOSONAR
        );
    }

    public SyncApplicationFactory(
            final HttpServer httpServer,
            final SyncEngineImpl syncEngine,
            final ExecutorService executorService,
            final URI uri
    ) {
        this(
                httpServer,
                syncEngine,
                executorService,
                uri,
                new SyncHandler(new WorkflowFactoryImpl(syncEngine))
        );

    }

    public SyncApplicationFactory(
            final HttpServer httpServer,
            final SyncEngineImpl syncEngine,
            final ExecutorService executorService,
            final URI uri,
            final SyncHandler syncHandler
    ) {

        this.httpServer = httpServer;
        this.syncEngine = syncEngine;
        this.executorService = executorService;
        this.uri = uri;
        this.syncHandler = syncHandler;
    }

    @Override
    public Application create()  {
        httpServer.setExecutor(executorService);
        syncEngine.addRequestChannel("", HttpClient.newBuilder().build(), HttpRequest.newBuilder().uri(uri));
        httpServer.createContext("/", syncHandler);
        return new SyncApplication(httpServer);
    }
}
