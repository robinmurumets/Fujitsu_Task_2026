package com.fujitsu.deliveryfee.exception;

public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a not-found exception with a client-facing message.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

