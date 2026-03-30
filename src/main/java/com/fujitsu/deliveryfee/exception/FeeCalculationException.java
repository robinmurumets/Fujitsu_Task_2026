package com.fujitsu.deliveryfee.exception;

public class FeeCalculationException extends RuntimeException {

    /**
     * Creates a fee calculation exception with a client-facing message.
     */
    public FeeCalculationException(String message) {
        super(message);
    }
}

