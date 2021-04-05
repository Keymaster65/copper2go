package de.wolfsvl.copper2go.workflowapi;

public interface EventChannel {
    void event(final String message);
    void errorEvent(final String message);
}
