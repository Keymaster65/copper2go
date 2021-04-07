package de.wolfsvl.copper2go.connector.http.vertx;

import de.wolfsvl.copper2go.engine.Copper2GoEngine;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class VertxHttpClientTest {

    public static final String CORRELATION_ID = "correlationId";
    public static final int SERVER_PORT = 8023;

    @Test
    void postGoodCase() throws InterruptedException {
        Copper2GoEngine engine = mock(Copper2GoEngine.class);
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        final String successResponse = "Success";
        httpServer.requestHandler(
                request -> request.handler(buffer -> {
                            final HttpServerResponse response = request.response();
                            response.end(successResponse);
                        }
                ));
        VertxHttpClient vertxHttpClient = new VertxHttpClient("localhost", SERVER_PORT, "/", engine);
        try {
            httpServer.listen(SERVER_PORT);
            vertxHttpClient.request(HttpMethod.POST, "Fault test.", CORRELATION_ID);
            Thread.sleep(3 * 1000);
        } finally {
            vertxHttpClient.stop();
            httpServer.close();
            vertx.close();
        }
        verify(engine).notify(CORRELATION_ID, successResponse);
    }

    @Test
    void postConnectionRefused() throws InterruptedException {
        Copper2GoEngine engine = mock(Copper2GoEngine.class);
        final VertxHttpClient vertxHttpClient = new VertxHttpClient("localhost", SERVER_PORT, "/", engine);
        vertxHttpClient.request(HttpMethod.GET,"Fault test.", CORRELATION_ID);
        Thread.sleep(5 * 1000); // connection refused max time
        verify(engine).notifyError(eq(CORRELATION_ID), anyString());
        verify(engine, times(0)).notify(any(), any());
    }

    @Test
    void startStop() {
        Copper2GoEngine engine = mock(Copper2GoEngine.class);
        final VertxHttpClient vertxHttpClient = new VertxHttpClient("localhost", SERVER_PORT, "/", engine);
        try {
            vertxHttpClient.start();
        } finally {
            vertxHttpClient.stop();
        }
    }
}