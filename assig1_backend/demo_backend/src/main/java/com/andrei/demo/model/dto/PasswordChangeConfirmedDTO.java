package com.andrei.demo.model.dto;

public record PasswordChangeConfirmedDTO (
        String oldPassword,
        String newPassword,
        String code
) {
}
