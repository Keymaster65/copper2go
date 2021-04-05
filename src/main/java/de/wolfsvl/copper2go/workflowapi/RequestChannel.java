package de.wolfsvl.copper2go.workflowapi;

public interface RequestChannel {
    void request(final String request, final String responseCorrelationId);
}
