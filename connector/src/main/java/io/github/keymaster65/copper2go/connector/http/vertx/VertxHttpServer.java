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

import com.google.common.io.CharStreams;
import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpServer;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.EngineException;
import io.github.keymaster65.copper2go.engine.WorkflowVersion;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class VertxHttpServer implements Copper2GoHttpServer {

    private static final Logger log = LoggerFactory.getLogger(VertxHttpServer.class);
    public static final String COPPER2GO_2_API = "/copper2go/2/api/";

    private final HttpServer httpServer;
    private final Vertx vertx;
    private final int port;


    public VertxHttpServer(final int port, final Copper2GoEngine copper2GoEngine) {
        this(port, copper2GoEngine, Vertx.vertx());
    }

    public VertxHttpServer(final int port, final Copper2GoEngine copper2GoEngine, final Vertx vertx) {
        this(port, copper2GoEngine, vertx, vertx.createHttpServer());
    }

    VertxHttpServer(final int port, final Copper2GoEngine copper2GoEngine, final Vertx vertx, final HttpServer httpServer) {
        this.port = port;
        this.vertx = vertx;
        this.httpServer = httpServer;
        httpServer.requestHandler(
                request -> request.bodyHandler(buffer -> {
                            final String requestBody;
                            requestBody = new String(buffer.getBytes(), StandardCharsets.UTF_8);
                            final HttpServerResponse response = request.response();
                            final String uri = request.uri();
                            if (uri.length() > 1 && uri.startsWith(COPPER2GO_2_API)) {
                                try {
                                    if (uri.startsWith(COPPER2GO_2_API + "request/") || uri.startsWith(COPPER2GO_2_API + "event/")) {
                                        WorkflowVersion workflowVersion = WorkflowVersion.of(uri);
                                        copper2GoEngine.callWorkflow(
                                                requestBody,
                                                new HttpReplyChannelImpl(response),
                                                workflowVersion.name,
                                                workflowVersion.major,
                                                workflowVersion.minor
                                        );
                                    } else {
                                        throw new IllegalArgumentException(String.format("PATH %s not as expected.", uri));
                                    }

                                    if (uri.startsWith(COPPER2GO_2_API + "event/")) {
                                        log.debug("Emtpy OK response for incoming event.");
                                        response
                                                .setStatusCode(HttpURLConnection.HTTP_ACCEPTED)
                                                .end();
                                    }
                                } catch (EngineException e) {
                                    response
                                            .setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                                            .end(String.format("Exception: %s", e.getMessage()));
                                    log.warn("Exception while calling workflow.", e);
                                }

                            } else {
                                try {
                                    String path;
                                    if ("/".equals(uri) || "/.".equals(uri)) {
                                        path = "license/index.html";
                                    } else {
                                        path = "license" + uri;
                                    }

                                    try (Reader reader = new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8)) {
                                        response
                                                .setStatusCode(HttpURLConnection.HTTP_NOT_FOUND)
                                                .end(CharStreams.toString(reader));
                                    }
                                } catch (Exception e) {
                                    response.end(String.format("Exception while getting licenses from uri %s. %s", uri, e.getMessage()));
                                }
                            }
                        }
                )
        );
    }

    @Override
    public void start() {
        log.info("Server listen on port {}", port);
        httpServer.listen(port);
    }

    @Override
    public void stop() {
        log.info("Stopping server.");
        httpServer.close(asyncResult -> log.info("Server close. succeeded={}", asyncResult.succeeded()));
        vertx.close(asyncResult -> log.info("VertX close. succeeded={}", asyncResult.succeeded()));
    }

}
