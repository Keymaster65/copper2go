package io.github.keymaster65.copper2go.connector.http.vertx;

import ch.qos.logback.classic.LoggerContext;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.util.Map;

class VertxHttpClientTest {

    private static final String EXCEPTION_MESSAGE = "Test message";
    private static final String CORRELATION_ID = "corrId";
    private static final String BODY = "body";
    private static final Map<String, String> NULL_ATTRIBUTES = null;
    private static final io.github.keymaster65.copper2go.connector.http.HttpMethod GET = io.github.keymaster65.copper2go.connector.http.HttpMethod.GET;

    @Test
    void createHttpRequestWithAttribute() {
        final WebClient webClient = Mockito.mock(WebClient.class);
        final HttpRequest<Buffer> httpRequest = createBufferHttpRequest(webClient);
        final VertxHttpClient vertxHttpClient = createVertxHttpClient(webClient, Mockito.mock(Vertx.class));

        final String parameterName = "parameterName";
        final String parameterValue = "parameterValue";
        Map<String, String> attributes = Map.of(parameterName, parameterValue);

        final HttpRequest<Buffer> result = vertxHttpClient.createHttpRequest(
                GET,
                attributes
        );

        Assertions.assertThat(result).isSameAs(httpRequest);
        Mockito.verify(httpRequest).addQueryParam(parameterName, parameterValue);
    }

    @Test
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

    @Test
    void successHandler() {
        resetLogContext();
        final Copper2GoEngine copper2GoEngine = Mockito.mock(Copper2GoEngine.class);
        @SuppressWarnings("unchecked")
        HttpResponse<Buffer> response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.bodyAsString()).thenReturn(BODY);

        final Handler<HttpResponse<Buffer>> handler = VertxHttpClient.successHandler(CORRELATION_ID, copper2GoEngine);
        handler.handle(response);

        Mockito.verify(copper2GoEngine).notify(CORRELATION_ID, BODY);
    }

    @Test
    void errorHandler() {
        resetLogContext();
        final Copper2GoEngine copper2GoEngine = Mockito.mock(Copper2GoEngine.class);
        final Handler<Throwable> handler = VertxHttpClient.errorHandler(CORRELATION_ID, copper2GoEngine);

        Throwable response = Mockito.mock(Throwable.class);
        Mockito.when(response.getMessage()).thenReturn(EXCEPTION_MESSAGE);
        handler.handle(response);

        Mockito.verify(copper2GoEngine).notifyError(CORRELATION_ID, EXCEPTION_MESSAGE);
    }

    @Test
    void close() {
        final WebClient webClient = Mockito.mock(WebClient.class);
        final Vertx vertx = Mockito.mock(Vertx.class);
        final VertxHttpClient vertxHttpClient = createVertxHttpClient(webClient, vertx);

        vertxHttpClient.close();

        Mockito.verify(webClient).close();
        Mockito.verify(vertx).close(Mockito.any());
    }


    @Test
    void request() {
        final WebClient webClient = Mockito.mock(WebClient.class);
        final HttpRequest<Buffer> httpRequest = createBufferHttpRequest(webClient);
        @SuppressWarnings("unchecked")
        final Future<HttpResponse<Buffer>> onFailureFuture = Mockito.mock(Future.class);
        @SuppressWarnings("unchecked")
        final Future<HttpResponse<Buffer>> onSuccessFuture = Mockito.mock(Future.class);
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
                Mockito.mock(Copper2GoEngine.class),
                vertx,
                webClient
        );
    }

    private static HttpRequest<Buffer> createBufferHttpRequest(final WebClient webClient) {
        @SuppressWarnings("unchecked") final HttpRequest<Buffer> httpRequest = Mockito.mock(HttpRequest.class);
        Mockito.when(webClient.request(
                HttpMethod.GET,
                0,
                "host",
                "uri"
        )).thenReturn(httpRequest);
        return httpRequest;
    }
}