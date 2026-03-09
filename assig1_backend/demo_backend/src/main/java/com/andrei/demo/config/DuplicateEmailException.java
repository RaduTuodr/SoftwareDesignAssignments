package com.andrei.demo.config;

public class DuplicateEmailException extends ValidationException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
