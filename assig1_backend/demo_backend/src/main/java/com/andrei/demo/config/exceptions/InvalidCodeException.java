package com.andrei.demo.config.exceptions;

public class InvalidCodeException extends ValidationException {
    public InvalidCodeException(String message) {
        super(message);
    }
}
