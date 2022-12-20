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
package io.github.keymaster65.copper2go.connector.http.vertx.receiver;

import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpServer;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import org.crac.Context;
import org.crac.Core;
import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VertxHttpServer implements Copper2GoHttpServer, Resource {

    private static final Logger log = LoggerFactory.getLogger(VertxHttpServer.class);

    private final HttpServer httpServer;
    private final Vertx vertx;
    private final int port;

    public VertxHttpServer(final int port, final Handler<HttpServerRequest> handler) {
        this(port, Vertx.vertx(), handler);
    }

    public VertxHttpServer(final int port, final Vertx vertx, final Handler<HttpServerRequest> handler) {
        this(port, vertx, vertx.createHttpServer(), handler);
    }

    VertxHttpServer(final int port, final Vertx vertx, final HttpServer httpServer, final Handler<HttpServerRequest> handler) {
        this.port = port;
        this.vertx = vertx;
        this.httpServer = httpServer;
        this.httpServer.requestHandler(handler);
        Core.getGlobalContext().register(this);
    }

    @Override
    public void start() {
        log.info("Before server listen on port {}", port);
        httpServer.listen(port);
        log.info("After server listen on port {}", port);
    }

    @Override
    public void stop() {
        log.info("Stopping server.");
        final Future<Void> closeHttpServerFuture = httpServer.close();
        log.info("Stopped httpServer with result {}", closeHttpServerFuture.result());

        final Future<Void> closeVertxFuture = vertx.close();
        log.info("Stopped vertx with result {}", closeVertxFuture.result());
    }

    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
        log.info("Stop httpServer in beforeCheckpoint.");

        final Future<Void> closeFuture = httpServer.close();
        log.info("Stopped httpServer with result {}", closeFuture.result());
    }

    @Override
    public void afterRestore(Context<? extends Resource> context) throws Exception {
        log.info("Start httpServer in afterRestore.");
        start();
    }
}