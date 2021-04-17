package de.wolfsvl.copper2go.connector.http;

import io.vertx.core.http.HttpMethod;

public interface Copper2GoHttpClient {
    void request(final HttpMethod httpMethod, final String request, final String responseCorrelationId);
    void close();
}
