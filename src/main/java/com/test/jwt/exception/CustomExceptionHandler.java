package com.test.jwt.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(EmptyTokenException.class)
    public ResponseEntity<?> emptyToken(EmptyTokenException e) {
        return ResponseEntity.badRequest().body(e.message);
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<?> expiredRefreshToken(RefreshTokenExpiredException e) {
        return ResponseEntity.badRequest().body(e.message);
    }

    @ExceptionHandler(AccessTokenExpiredException.class)
    public ResponseEntity<?> expiredAccessToken(AccessTokenExpiredException e) {
        return ResponseEntity.badRequest().body(e.message);
    }
}
