package io.github.keymaster65.copper2go.engine;

public interface EventChannel {
    void event(final String message);
    void errorEvent(final String message);
}
