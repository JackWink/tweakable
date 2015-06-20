package com.jackwink.tweakable.exceptions;

/**
 * Thrown when too many preferences are in use, this is both for memory concerns
 * as well as usability.
 */
public class TooManyPreferencesException extends RuntimeException {

    public TooManyPreferencesException(String reason) {
        super(reason);
    }

    public TooManyPreferencesException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
