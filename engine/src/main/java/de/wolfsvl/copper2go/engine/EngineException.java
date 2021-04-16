package de.wolfsvl.copper2go.engine;

public class EngineException extends Exception {
    private static final long serialVersionUID = 1L;

    public EngineException(final String message) {
        super(message);
    }
    public EngineException(final String message, final Exception cause) {
        super(message, cause);
    }
}
