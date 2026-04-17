package com.andrei.demo.config.exceptions;

public class DuplicateEmailException extends ValidationException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
