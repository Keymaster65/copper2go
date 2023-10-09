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

import io.github.keymaster65.copper2go.api.connector.ResponseReceiver;
import io.github.keymaster65.copper2go.connector.http.HttpMethod;
import io.github.keymaster65.copper2go.connector.http.vertx.receiver.VertxHttpClient;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

class VertxHttpClientTest {

    public static final String CORRELATION_ID = "correlationId";
    public static final int SERVER_PORT = 8023;
    public static final String LOCALHOST = "localhost";

    private static final Logger log = LoggerFactory.getLogger(VertxHttpClientTest.class);

    @Test
    @Timeout(30)
    void postGoodCase() throws InterruptedException {
        final CountDownLatch clientLatch = new CountDownLatch(1);

        final ResponseReceiver responseReceiver = new ResponseReceiver() {
            @Override
            public void receive(final String responseCorrelationId, final String response) {
                clientLatch.countDown();
            }

            @Override
            public void receiveError(final String responseCorrelationId, final String response) {
                throw new UnsupportedOperationException("receiveError not expected");
            }
        };

        final Vertx vertx = Vertx.vertx();
        final HttpServer httpServer = vertx.createHttpServer();
        final String successResponse = "Success";
        final CountDownLatch serverLatch = new CountDownLatch(1);
        httpServer.requestHandler(
                request -> request.handler(buffer -> {
                            final HttpServerResponse response = request.response();
                            response.end(successResponse);
                            log.info("end response {}.", successResponse);
                            serverLatch.countDown();
                        }
                ));
        final VertxHttpClient vertxHttpClient =
                new VertxHttpClient(LOCALHOST, SERVER_PORT, "/", responseReceiver);
        try {
            httpServer.listen(SERVER_PORT).onComplete(
                    result -> {
                        log.info("end result {}.", result);
                        vertxHttpClient.request(HttpMethod.valueOf("POST"), "Fault test.", CORRELATION_ID);
                    }
            );
            serverLatch.await();
        } finally {
            httpServer.close();
            vertxHttpClient.close();
            vertx.close();
        }

        Assertions
                .assertThatCode(clientLatch::await)
                .doesNotThrowAnyException();
    }

    @Test
    @Timeout(10)
    void postConnectionRefused() {
        final CountDownLatch clientLatch = new CountDownLatch(1);
        final ResponseReceiver responseReceiver = new ResponseReceiver() {
            @Override
            public void receive(final String responseCorrelationId, final String response) {
                throw new UnsupportedOperationException("Receive not expected");

            }

            @Override
            public void receiveError(final String responseCorrelationId, final String response) {
                Assertions
                        .assertThat(response)
                        .contains("Connection refused");
                clientLatch.countDown();
            }
        };


        final VertxHttpClient vertxHttpClient = new VertxHttpClient(LOCALHOST, 50666, "/", responseReceiver);
        try {
            vertxHttpClient.request(HttpMethod.GET, "Fault test.", CORRELATION_ID);
        } finally {
            vertxHttpClient.close();
        }


        Assertions
                .assertThatCode(clientLatch::await)
                .doesNotThrowAnyException();
    }

    @Test
    void close() {
        ResponseReceiver responseReceiver = Mockito.mock(ResponseReceiver.class);


        final VertxHttpClient vertxHttpClient = new VertxHttpClient(LOCALHOST, SERVER_PORT, "/", responseReceiver);


        Assertions
                .assertThatCode(vertxHttpClient::close)
                .doesNotThrowAnyException();
    }
}