package com.test.jwt.exception;


import java.util.HashMap;
import java.util.Map;

public class AccessTokenExpiredException extends RuntimeException {
    Map<String, String> message = new HashMap<>();

    public AccessTokenExpiredException(String message, String newAccessToken) {
        super(message);
        this.message.put("message", message);
        this.message.put("AccessToken", "Bearer " + newAccessToken);
    }
}
