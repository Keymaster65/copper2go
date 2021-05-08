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

import io.github.keymaster65.copper2go.connector.http.TestHttpClient;
import io.github.keymaster65.copper2go.connector.http.vertx.RequestHandler;
import io.github.keymaster65.copper2go.connector.http.vertx.VertxHttpServer;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.EngineException;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;

import static io.github.keymaster65.copper2go.connector.http.vertx.RequestHandler.COPPER2GO_2_API;

class VertxHttpServerTest {

    public static final int SERVER_PORT = 8024;

    @Test
    void startStop() {
        Copper2GoEngine engine = Mockito.mock(Copper2GoEngine.class);
        final VertxHttpServer vertxHttpServer = new VertxHttpServer(SERVER_PORT, new RequestHandler(engine));
        try {
            vertxHttpServer.start();
        } finally {
            vertxHttpServer.stop();
        }
    }

    @Test
    void post() throws InterruptedException, EngineException, IOException {
        Copper2GoEngine engine = Mockito.mock(Copper2GoEngine.class);

        // Exception should lead to normal response.end() if no workflow does
        Mockito.doThrow(new EngineException("Simulated exception."))
                .when(engine)
                .callWorkflow(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq("Hello"), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(0L));

        final Vertx vertx = Vertx.vertx();
        final VertxHttpServer vertxHttpServer = new VertxHttpServer(SERVER_PORT, vertx, new RequestHandler(engine));
        vertxHttpServer.start();
        TestHttpClient.post(URI.create("http://localhost:" + SERVER_PORT + COPPER2GO_2_API + "request/1.0/Hello"), "Wolf\r\n");
        vertxHttpServer.stop();

        Mockito.verify(engine).callWorkflow(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq("Hello"), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(0L));
    }
}