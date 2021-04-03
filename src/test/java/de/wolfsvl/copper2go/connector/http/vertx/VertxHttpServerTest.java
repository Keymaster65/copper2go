package de.wolfsvl.copper2go.connector.http.vertx;

import de.wolfsvl.copper2go.engine.Copper2GoEngine;
import de.wolfsvl.copper2go.engine.EngineException;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import org.copperengine.core.CopperRuntimeException;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class VertxHttpServerTest {

    @Test
    void startStop() {
        Copper2GoEngine engine = mock(Copper2GoEngine.class);
        final VertxHttpServer vertxHttpServer = new VertxHttpServer(8023, engine);
        try {
            vertxHttpServer.start();
        } finally {
            vertxHttpServer.stop();
        }
    }

    @Test
    void post() throws InterruptedException, EngineException {
        Copper2GoEngine engine = mock(Copper2GoEngine.class);
        doThrow(new EngineException("Simulated exception.")).when(engine).callWorkflow(any());
        final int port = 8023;
        final Vertx vertx = Vertx.vertx();
        final VertxHttpServer vertxHttpServer = new VertxHttpServer(port, engine);
        WebClient client = WebClient.create(vertx);
        try {
            vertxHttpServer.start();

            client
                    .post(port, "localhost", "/hello")
                    .sendBuffer(Buffer.buffer("Wolf\r\n"))
                    .onFailure(err -> System.out.println("Failure=" + err.getMessage()))
                    .onSuccess(result -> System.out.println("Result=" + result.bodyAsString()));
            Thread.sleep(3000);

        } finally {
            vertxHttpServer.stop();
            client.close();
        }

        verify(engine).callWorkflow(any());
    }
}