package io.github.keymaster65.copper2go.connector.standardio;

public class StandardInOutException extends Exception {
    private static final long serialVersionUID = 1;

    public StandardInOutException(final String message) {
        super(message);
    }

    public StandardInOutException(final String message, final Exception cause) {
        super(message, cause);
    }
}
