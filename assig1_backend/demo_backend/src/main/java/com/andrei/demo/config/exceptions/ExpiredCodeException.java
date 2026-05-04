package com.andrei.demo.config.exceptions;

public class ExpiredCodeException extends ValidationException {
    public ExpiredCodeException(String message) {
        super(message);
    }
}
