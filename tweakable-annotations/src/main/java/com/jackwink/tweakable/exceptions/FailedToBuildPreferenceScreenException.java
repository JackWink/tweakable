package com.jackwink.tweakable.exceptions;

/**
 * Thrown when we can't build a {@link android.preference.PreferenceScreen}
 */
public class FailedToBuildPreferenceScreenException extends RuntimeException {
    public static final long serialVersionUID = 0L;

    public FailedToBuildPreferenceScreenException(String reason) {
        super(reason);
    }

    public FailedToBuildPreferenceScreenException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
