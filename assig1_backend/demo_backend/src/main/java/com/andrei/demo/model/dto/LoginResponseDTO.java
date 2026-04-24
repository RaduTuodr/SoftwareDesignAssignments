package com.andrei.demo.model.dto;

public record LoginResponseDTO(
        Boolean success,
        String role,
        String accessToken,
        Integer expiresAt,
        String errorMessage
) {
}