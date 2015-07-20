package com.jackwink.tweakable.exceptions;

public class FailedToBuildPreferenceCategoryException extends RuntimeException {
    public static final long serialVersionUID = 0L;

    public FailedToBuildPreferenceCategoryException(String reason) {
        super(reason);
    }

    public FailedToBuildPreferenceCategoryException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }

}
