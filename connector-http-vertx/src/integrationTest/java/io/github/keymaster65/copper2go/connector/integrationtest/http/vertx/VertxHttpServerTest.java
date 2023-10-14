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

import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.api.connector.PayloadReceiver;
import io.github.keymaster65.copper2go.connector.http.TestHttpClient;
import io.github.keymaster65.copper2go.connector.http.vertx.receiver.ApiPath;
import io.github.keymaster65.copper2go.connector.http.vertx.receiver.RequestHandler;
import io.github.keymaster65.copper2go.connector.http.vertx.receiver.VertxHttpServer;
import io.vertx.core.Vertx;
import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;
import org.crac.Context;
import org.crac.Resource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;

import static org.mockito.ArgumentMatchers.eq;

class VertxHttpServerTest {

    public static final int SERVER_PORT = 8024;

    @Example
    void startStop() {
        PayloadReceiver payloadReceiver = Mockito.mock(PayloadReceiver.class);
        final VertxHttpServer vertxHttpServer = new VertxHttpServer(SERVER_PORT, new RequestHandler(payloadReceiver));
        try {
            Assertions
                    .assertThatNoException()
                    .isThrownBy(vertxHttpServer::start);
        } finally {
            Assertions
                    .assertThatNoException()
                    .isThrownBy(vertxHttpServer::stop);
        }
    }

    @Example
    void post() throws InterruptedException, EngineException, IOException {
        PayloadReceiver payloadReceiver = Mockito.mock(PayloadReceiver.class);

        // Exception should lead to normal response.end() if no workflow does
        Mockito.doThrow(new EngineException("Simulated exception."))
                .when(payloadReceiver)
                .receive(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any(),
                        eq("Hello"),
                        eq(1L),
                        eq(0L));

        final Vertx vertx = Vertx.vertx();
        final VertxHttpServer vertxHttpServer = new VertxHttpServer(SERVER_PORT, vertx, new RequestHandler(payloadReceiver));
        try {
            vertxHttpServer.start();
            TestHttpClient.post(URI.create("http://localhost:" + SERVER_PORT + ApiPath.TWOWAY_PATH + "1.0/Hello"), "Wolf\r\n");
        } finally {
            vertxHttpServer.stop();
        }

        Mockito.verify(payloadReceiver).receive(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                eq("Hello"),
                eq(1L),
                eq(0L));
    }

    @Example
    void cracBeforeAfter() {
        final PayloadReceiver payloadReceiver = Mockito.mock(PayloadReceiver.class);
        @SuppressWarnings("unchecked")
        final Context<? extends Resource> context = Mockito.mock(Context.class);

        final VertxHttpServer vertxHttpServer = new VertxHttpServer(SERVER_PORT, new RequestHandler(payloadReceiver));
        try {
            Assertions
                    .assertThatNoException()
                    .isThrownBy(() -> vertxHttpServer.afterRestore(context));
        } finally {
            Assertions
                    .assertThatNoException()
                    .isThrownBy(() -> vertxHttpServer.beforeCheckpoint(context));
        }
    }
}