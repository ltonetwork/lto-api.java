package com.ltonetwork.client.exceptions;

@SuppressWarnings("serial")
public class InvalidAccountException extends RuntimeException {
    public InvalidAccountException(String message) {
        super(message);
    }
}
