package com.jackwink.tweakable.exceptions;

/**
 * Thrown when a {@link com.jackwink.tweakable.generators.java.JavaBuilder} fails to
 * create an object.
 */
public class FailedToBuildPreferenceException extends RuntimeException {
    public static final long serialVersionUID = 0L;

    public FailedToBuildPreferenceException(String reason) {
        super(reason);
    }

    public FailedToBuildPreferenceException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
