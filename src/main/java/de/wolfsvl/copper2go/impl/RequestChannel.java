package de.wolfsvl.copper2go.impl;

public interface RequestChannel {
    void request(final String request, final String responseCorrelationId);
}
