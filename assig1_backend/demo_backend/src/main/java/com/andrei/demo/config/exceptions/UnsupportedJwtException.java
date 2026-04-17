package com.andrei.demo.config.exceptions;

public class UnsupportedJwtException extends RuntimeException {
    public UnsupportedJwtException(String message) {
        super(message);
    }
}
