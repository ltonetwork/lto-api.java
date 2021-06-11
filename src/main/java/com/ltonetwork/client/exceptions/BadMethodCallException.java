package com.ltonetwork.client.exceptions;

@SuppressWarnings("serial")
public class BadMethodCallException extends RuntimeException {
    public BadMethodCallException(String message) {
        super(message);
    }
}