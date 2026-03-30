package com.fujitsu.deliveryfee.exception;

public class WeatherImportException extends RuntimeException {

    /**
     * Creates a weather import exception with the original cause when available.
     */
    public WeatherImportException(String message, Throwable cause) {
        super(message, cause);
    }
}

