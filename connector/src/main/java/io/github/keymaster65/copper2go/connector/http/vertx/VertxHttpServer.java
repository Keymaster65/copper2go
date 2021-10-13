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
package io.github.keymaster65.copper2go.connector.http.vertx;

import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpServer;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxHttpServer implements Copper2GoHttpServer {

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
    }

    @Override
    public void start() {
        log.info("Before server listen on port {}", port);
        final Future<HttpServer> listen = httpServer.listen(port);
        log.info("After server listen on port {} completed={} failed={} cause={}", port, listen.isComplete(), listen.failed(), listen.cause());
    }

    @Override
    public void stop() {
        log.info("Stopping server.");
        httpServer.close(asyncResult -> log.info("Server close. succeeded={}", asyncResult.succeeded()));
        vertx.close(asyncResult -> log.info("VertX close. succeeded={}", asyncResult.succeeded()));
    }
}
