package com.jackwink.tweakable.exceptions;

/**
 * Thrown when either an IO error happens, or other exception causes the Xml build process to fail.
 */
public class FailedToBuildXmlException extends RuntimeException {

    public FailedToBuildXmlException(String reason) {
        super(reason);
    }

    public FailedToBuildXmlException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
