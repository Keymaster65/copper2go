package de.wolfsvl.copper2go.connector.http.vertx;

import de.wolfsvl.copper2go.engine.Copper2GoEngine;
import de.wolfsvl.copper2go.engine.EngineException;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

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

        // Exception should lead to normal response.end() ig no workflow does
        doThrow(new EngineException("Simulated exception.")).when(engine).callWorkflow(any());

        final int port = 8024;
        final Vertx vertx = Vertx.vertx();
        final VertxHttpServer vertxHttpServer = new VertxHttpServer(port, engine, vertx);
        BlockingQueue<Throwable> blockingQueue = new SynchronousQueue<>();
        WebClient client = WebClient.create(vertx);
        vertxHttpServer.start();

        client
                .post(port, "localhost", "/hello")
                .sendBuffer(Buffer.buffer("Wolf\r\n"))
                .onFailure(err -> {
                    try {
                        System.out.println("Failure=" + err.getMessage());
                        blockingQueue.add(new RuntimeException(err.getMessage()));
                    } finally {
                        vertxHttpServer.stop();
                        client.close();
                    }
                })
                .onSuccess(result -> {
                    try {
                        System.out.println("Result=" + result.bodyAsString());
                        verify(engine).callWorkflow(any());
                        blockingQueue.add(new PositivResult());
                    } catch (Throwable e) {
                        blockingQueue.add(e);
                    } finally {
                        vertxHttpServer.stop();
                        client.close();
                    }
                });
        Throwable result = blockingQueue.take();
        Assertions.assertThatExceptionOfType(PositivResult.class).isThrownBy(() -> {
            throw result;
        });
    }

    private static class PositivResult extends Throwable {
    }
}