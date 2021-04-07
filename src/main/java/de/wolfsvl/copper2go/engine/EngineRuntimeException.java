package de.wolfsvl.copper2go.engine;

public class EngineRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EngineRuntimeException(final String message, final Exception cause) {
        super(message, cause);
    }
}
