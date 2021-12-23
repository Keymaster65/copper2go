package io.github.keymaster65.copper2go.connector.http.vertx;

import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

class VertxHttpClientTest {

    @Test
    void createHttpRequest() {
        final WebClient webClient = Mockito.mock(WebClient.class);
        @SuppressWarnings("unchecked")
        final HttpRequest<Buffer> httpRequest = Mockito.mock(HttpRequest.class);
        Mockito.when(webClient.request(
                HttpMethod.GET,
                0,
                "host",
                "uri"

        )).thenReturn(httpRequest);
        final VertxHttpClient vertxHttpClient = new VertxHttpClient(
                "host",
                0,
                "uri",
                Mockito.mock(Copper2GoEngine.class),
                Mockito.mock(Vertx.class),
                webClient
        );
        final String parameterName = "parameterName";
        final String parameterValue = "parameterValue";
        Map<String, String> attributes = Map.of(parameterName, parameterValue);

        final HttpRequest<Buffer> result = vertxHttpClient.createHttpRequest(
                io.github.keymaster65.copper2go.connector.http.HttpMethod.GET,
                attributes
        );

        Assertions.assertThat(result).isSameAs(httpRequest);
        Mockito.verify(httpRequest).addQueryParam(parameterName, parameterValue);
    }
}