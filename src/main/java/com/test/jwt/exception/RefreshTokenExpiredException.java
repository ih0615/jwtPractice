package com.test.jwt.exception;

public class RefreshTokenExpiredException extends RuntimeException {

    String message;

    public RefreshTokenExpiredException(String message) {
        super(message);
        this.message = message;
    }
}
