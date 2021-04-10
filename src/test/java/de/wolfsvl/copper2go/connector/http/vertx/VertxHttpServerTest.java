package de.wolfsvl.copper2go.connector.http.vertx;

import de.wolfsvl.copper2go.engine.Copper2GoEngine;
import de.wolfsvl.copper2go.engine.EngineException;
import de.wolfsvl.copper2go.testutil.TestHttpClient;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class VertxHttpServerTest {

    public static final int SERVER_PORT = 8024;

    @Test
    void startStop() {
        Copper2GoEngine engine = mock(Copper2GoEngine.class);
        final VertxHttpServer vertxHttpServer = new VertxHttpServer(SERVER_PORT, engine);
        try {
            vertxHttpServer.start();
        } finally {
            vertxHttpServer.stop();
        }
    }

    @Test
    void post() throws InterruptedException, EngineException, IOException {
        Copper2GoEngine engine = mock(Copper2GoEngine.class);

        // Exception should lead to normal response.end() if no workflow does
        doThrow(new EngineException("Simulated exception."))
                .when(engine)
                .callWorkflow(any(), eq("Hello"), eq(1L), eq(0L));

        final Vertx vertx = Vertx.vertx();
        final VertxHttpServer vertxHttpServer = new VertxHttpServer(SERVER_PORT, engine, vertx);
        vertxHttpServer.start();
        TestHttpClient.post(URI.create("http://localhost:" + SERVER_PORT + "/demo/1.0/Hello"), "Wolf\r\n");
        vertxHttpServer.stop();

        verify(engine).callWorkflow(any(), eq("Hello"), eq(1L), eq(0L));
    }
}