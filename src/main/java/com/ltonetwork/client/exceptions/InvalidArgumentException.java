package com.ltonetwork.client.exceptions;

@SuppressWarnings("serial")
public class InvalidArgumentException extends RuntimeException {
    public InvalidArgumentException(String message) {
        super(message);
    }
}
