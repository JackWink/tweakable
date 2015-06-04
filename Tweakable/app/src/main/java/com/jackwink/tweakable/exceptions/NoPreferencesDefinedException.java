package com.jackwink.tweakable.exceptions;

/**
 * Thrown when there are no preferences defined in a category at build-time.
 */
public class NoPreferencesDefinedException extends RuntimeException {

    public NoPreferencesDefinedException(String reason) {
        super(reason);
    }

    public NoPreferencesDefinedException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
