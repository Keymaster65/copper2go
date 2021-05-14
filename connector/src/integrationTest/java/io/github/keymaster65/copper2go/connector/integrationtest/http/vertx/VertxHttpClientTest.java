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
package io.github.keymaster65.copper2go.connector.integrationtest.http.vertx;

import io.github.keymaster65.copper2go.connector.http.HttpMethod;
import io.github.keymaster65.copper2go.connector.http.vertx.VertxHttpClient;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;

class VertxHttpClientTest {

    public static final String CORRELATION_ID = "correlationId";
    public static final int SERVER_PORT = 8023;
    public static final String LOCALHOST = "localhost";

    @Test
    void postGoodCase() throws InterruptedException {
        Copper2GoEngine engine = Mockito.mock(Copper2GoEngine.class);
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        final String successResponse = "Success";
        CountDownLatch latch = new CountDownLatch(1);
        httpServer.requestHandler(
                request -> request.handler(buffer -> {
                            final HttpServerResponse response = request.response();
                            response.end(successResponse);
                            latch.countDown();
                        }
                ));
        VertxHttpClient vertxHttpClient = new VertxHttpClient(LOCALHOST, SERVER_PORT, "/", engine);
        try {
            httpServer.listen(SERVER_PORT);
            vertxHttpClient.request(HttpMethod.valueOf("POST"), "Fault test.", CORRELATION_ID);
            latch.await();
            Thread.sleep(1000); // give client time for async processing
        } finally {
            httpServer.close();
            vertxHttpClient.close();
            vertx.close();
        }

        Mockito.verify(engine).notify(CORRELATION_ID, successResponse);
    }

    // disabled due to hanging on Jenkins
    void postConnectionRefused() throws InterruptedException {
        Copper2GoEngine engine = Mockito.mock(Copper2GoEngine.class);
        final VertxHttpClient vertxHttpClient = new VertxHttpClient(LOCALHOST, 50666, "/", engine);
        vertxHttpClient.request(HttpMethod.GET, "Fault test.", CORRELATION_ID);
        Thread.sleep(5L * 1000); // connection refused max time
        vertxHttpClient.close();
        Mockito.verify(engine).notifyError(ArgumentMatchers.eq(CORRELATION_ID), ArgumentMatchers.anyString());
        Mockito.verify(engine, Mockito.times(0)).notify(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void close() {
        Copper2GoEngine engine = Mockito.mock(Copper2GoEngine.class);
        final VertxHttpClient vertxHttpClient = new VertxHttpClient(LOCALHOST, SERVER_PORT, "/", engine);
        vertxHttpClient.close();
    }
}