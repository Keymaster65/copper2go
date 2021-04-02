package de.wolfsvl.copper2go.connector.standardio;

public class StandardInOutException extends Exception {
    public StandardInOutException(final String message) {
        super(message);
    }

    public StandardInOutException(final String message, final Exception cause) {
        super(message, cause);
    }
}
