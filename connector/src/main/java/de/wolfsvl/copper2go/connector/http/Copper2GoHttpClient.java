package de.wolfsvl.copper2go.connector.http;

public interface Copper2GoHttpClient {
    void request(final HttpMethod httpMethod, final String request, final String responseCorrelationId);
    void close();
}
