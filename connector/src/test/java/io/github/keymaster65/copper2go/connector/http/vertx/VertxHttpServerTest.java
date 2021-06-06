package io.github.keymaster65.copper2go.connector.http.vertx;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class VertxHttpServerTest {

    @Test
    void startStop() {
        final HttpServer httpServer = mock(HttpServer.class);
        //noinspection unchecked
        final VertxHttpServer vertxHttpServer = new VertxHttpServer(
                0,
                mock(Vertx.class),
                httpServer,
                mock(Handler.class)
        );

        vertxHttpServer.start();
        verify(httpServer).listen(anyInt());

        vertxHttpServer.stop();
        verify(httpServer).close(any());
    }
}