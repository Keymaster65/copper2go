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