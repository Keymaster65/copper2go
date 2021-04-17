package de.wolfsvl.copper2go.connector.http.vertx;

import de.wolfsvl.copper2go.connector.http.HttpMethod;
import de.wolfsvl.copper2go.connector.http.vertx.VertxHttpClient;
import de.wolfsvl.copper2go.engine.Copper2GoEngine;
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
        VertxHttpClient vertxHttpClient = new VertxHttpClient("localhost", SERVER_PORT, "/", engine);
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
        final VertxHttpClient vertxHttpClient = new VertxHttpClient("localhost", 50666, "/", engine);
        vertxHttpClient.request(HttpMethod.GET, "Fault test.", CORRELATION_ID);
        Thread.sleep(5 * 1000); // connection refused max time
        vertxHttpClient.close();
        Mockito.verify(engine).notifyError(ArgumentMatchers.eq(CORRELATION_ID), ArgumentMatchers.anyString());
        Mockito.verify(engine, Mockito.times(0)).notify(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void close() {
        Copper2GoEngine engine = Mockito.mock(Copper2GoEngine.class);
        final VertxHttpClient vertxHttpClient = new VertxHttpClient("localhost", SERVER_PORT, "/", engine);
        vertxHttpClient.close();
    }
}