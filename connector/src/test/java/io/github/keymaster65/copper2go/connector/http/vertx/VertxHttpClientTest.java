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

import ch.qos.logback.classic.LoggerContext;
import io.github.keymaster65.copper2go.engine.Engine;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.util.Map;

class VertxHttpClientTest {

    private static final String EXCEPTION_MESSAGE = "Test message";
    private static final String CORRELATION_ID = "corrId";
    private static final String BODY = "body";
    private static final Map<String, String> NULL_ATTRIBUTES = null;
    private static final io.github.keymaster65.copper2go.connector.http.HttpMethod GET = io.github.keymaster65.copper2go.connector.http.HttpMethod.GET;

    @Property
    void createHttpRequestWithAttribute(@ForAll final io.github.keymaster65.copper2go.connector.http.HttpMethod httpMethod) {
        final WebClient webClient = Mockito.mock(WebClient.class);
        final HttpRequest<Buffer> httpRequest = createBufferHttpRequest(webClient);
        final VertxHttpClient vertxHttpClient = createVertxHttpClient(webClient, Mockito.mock(Vertx.class));

        final String parameterName = "parameterName";
        final String parameterValue = "parameterValue";
        Map<String, String> attributes = Map.of(parameterName, parameterValue);

        final HttpRequest<Buffer> result = vertxHttpClient.createHttpRequest(
                httpMethod,
                attributes
        );

        Assertions.assertThat(result).isSameAs(httpRequest);
        Mockito.verify(httpRequest).addQueryParam(parameterName, parameterValue);
    }

    @Example
    void createHttpRequestNullAttributes() {
        final WebClient webClient = Mockito.mock(WebClient.class);
        final HttpRequest<Buffer> httpRequest = createBufferHttpRequest(webClient);
        final VertxHttpClient vertxHttpClient = createVertxHttpClient(webClient, Mockito.mock(Vertx.class));

        final HttpRequest<Buffer> result = vertxHttpClient.createHttpRequest(
                GET,
                NULL_ATTRIBUTES
        );

        Assertions.assertThat(result).isSameAs(httpRequest);
        Mockito.verifyNoInteractions(httpRequest);
    }

    @Example
    void successHandler() {
        resetLogContext();
        final Engine copper2GoEngine = Mockito.mock(Engine.class);
        @SuppressWarnings("unchecked")
        HttpResponse<Buffer> response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.bodyAsString()).thenReturn(BODY);

        final Handler<HttpResponse<Buffer>> handler = VertxHttpClient.successHandler(CORRELATION_ID, copper2GoEngine);
        handler.handle(response);

        Mockito.verify(copper2GoEngine).notify(CORRELATION_ID, BODY);
    }

    @Example
    void errorHandler() {
        resetLogContext();
        final Engine copper2GoEngine = Mockito.mock(Engine.class);
        final Handler<Throwable> handler = VertxHttpClient.errorHandler(CORRELATION_ID, copper2GoEngine);

        Throwable response = Mockito.mock(Throwable.class);
        Mockito.when(response.getMessage()).thenReturn(EXCEPTION_MESSAGE);
        handler.handle(response);

        Mockito.verify(copper2GoEngine).notifyError(CORRELATION_ID, EXCEPTION_MESSAGE);
    }

    @Example
    void close() {
        final WebClient webClient = Mockito.mock(WebClient.class);
        final Vertx vertx = Mockito.mock(Vertx.class);
        final VertxHttpClient vertxHttpClient = createVertxHttpClient(webClient, vertx);

        vertxHttpClient.close();

        Mockito.verify(webClient).close();
        Mockito.verify(vertx).close(Mockito.any());
    }


    @Example
    void request() {
        final WebClient webClient = Mockito.mock(WebClient.class);
        final HttpRequest<Buffer> httpRequest = createBufferHttpRequest(webClient);
        @SuppressWarnings("unchecked") final Future<HttpResponse<Buffer>> onFailureFuture = Mockito.mock(Future.class);
        @SuppressWarnings("unchecked") final Future<HttpResponse<Buffer>> onSuccessFuture = Mockito.mock(Future.class);
        Mockito.when(onFailureFuture.onFailure(Mockito.any())).thenReturn(onSuccessFuture);
        Mockito.when(httpRequest.sendBuffer(Mockito.any())).thenReturn(onFailureFuture);
        final Vertx vertx = Mockito.mock(Vertx.class);
        final VertxHttpClient vertxHttpClient = createVertxHttpClient(webClient, vertx);

        vertxHttpClient.request(GET, BODY, CORRELATION_ID, null);

        Mockito.verify(onSuccessFuture).onSuccess(Mockito.any());
    }

    /**
     * Enables "debug" logging.
     */
    public static void resetLogContext() {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
    }

    private static VertxHttpClient createVertxHttpClient(
            final WebClient webClient,
            final Vertx vertx
    ) {
        return new VertxHttpClient(
                "host",
                0,
                "uri",
                Mockito.mock(Engine.class),
                vertx,
                webClient
        );
    }

    private static HttpRequest<Buffer> createBufferHttpRequest(final WebClient webClient) {
        @SuppressWarnings("unchecked") final HttpRequest<Buffer> httpRequest = Mockito.mock(HttpRequest.class);
        Mockito.when(webClient.request(
                Mockito.any(),
                Mockito.eq(0),
                Mockito.eq("host"),
                Mockito.eq("uri")
        )).thenReturn(httpRequest);
        return httpRequest;
    }
}