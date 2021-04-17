package de.wolfsvl.copper2go.engine;

public interface RequestChannel {
    void request(final String request, final String responseCorrelationId);
}
