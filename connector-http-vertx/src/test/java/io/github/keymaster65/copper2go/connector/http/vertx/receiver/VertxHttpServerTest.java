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
package io.github.keymaster65.copper2go.connector.http.vertx.receiver;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;
import org.crac.Context;
import org.crac.Resource;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VertxHttpServerTest {

    @SuppressWarnings("unchecked")
    @Example
    void construct() {
        Assertions
                .assertThatCode(() -> new VertxHttpServer(
                        0,
                        mock(Handler.class)
                ))
                .doesNotThrowAnyException();
    }

    @Example
    void startStop() {
        final HttpServer httpServer = mock(HttpServer.class);
        @SuppressWarnings("unchecked") final Future<Void> httpServerCloseFuture = mock(Future.class);
        when(httpServer.close()).thenReturn(httpServerCloseFuture);
        @SuppressWarnings("unchecked") final Handler<HttpServerRequest> handler = mock(Handler.class);
        @SuppressWarnings("unchecked") final Future<Void> vertxCloseFuture = mock(Future.class);
        final Vertx vertx = mock(Vertx.class);
        when(vertx.close()).thenReturn(vertxCloseFuture);
        final VertxHttpServer vertxHttpServer = new VertxHttpServer(
                0,
                vertx,
                httpServer,
                handler
        );

        vertxHttpServer.start();
        verify(httpServer).listen(anyInt());

        vertxHttpServer.stop();
        verify(httpServer).close();
    }


    @Example
    void cracBeforeAfter() {
        @SuppressWarnings("unchecked") final Context<? extends Resource> context = Mockito.mock(Context.class);

        final HttpServer httpServer = mock(HttpServer.class);
        @SuppressWarnings("unchecked") final Future<Void> httpServerCloseFuture = mock(Future.class);
        when(httpServer.close()).thenReturn(httpServerCloseFuture);
        @SuppressWarnings("unchecked") final Handler<HttpServerRequest> handler = mock(Handler.class);
        @SuppressWarnings("unchecked") final Future<Void> vertxCloseFuture = mock(Future.class);
        final Vertx vertx = mock(Vertx.class);
        when(vertx.close()).thenReturn(vertxCloseFuture);
        final VertxHttpServer vertxHttpServer = new VertxHttpServer(
                8024,
                vertx,
                httpServer,
                handler
        );


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