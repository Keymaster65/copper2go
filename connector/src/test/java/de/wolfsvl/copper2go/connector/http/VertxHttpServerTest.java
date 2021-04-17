package de.wolfsvl.copper2go.connector.http;

import de.wolfsvl.copper2go.connector.http.vertx.VertxHttpServer;
import de.wolfsvl.copper2go.engine.Copper2GoEngine;
import de.wolfsvl.copper2go.engine.EngineException;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;

class VertxHttpServerTest {

    public static final int SERVER_PORT = 8024;

    @Test
    void startStop() {
        Copper2GoEngine engine = Mockito.mock(Copper2GoEngine.class);
        final VertxHttpServer vertxHttpServer = new VertxHttpServer(SERVER_PORT, engine);
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
        final VertxHttpServer vertxHttpServer = new VertxHttpServer(SERVER_PORT, engine, vertx);
        vertxHttpServer.start();
        TestHttpClient.post(URI.create("http://localhost:" + SERVER_PORT + "/demo/1.0/Hello"), "Wolf\r\n");
        vertxHttpServer.stop();

        Mockito.verify(engine).callWorkflow(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq("Hello"), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(0L));
    }
}