package com.test.jwt.exception;

public class EmptyTokenException extends RuntimeException {

    String message;

    public EmptyTokenException(String message) {
        super(message);
        this.message = message;
    }
}
