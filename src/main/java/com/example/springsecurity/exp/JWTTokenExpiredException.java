package com.example.springsecurity.exp;

public class JWTTokenExpiredException extends RuntimeException {
    public JWTTokenExpiredException(String message) {
        super(message);
    }
}
