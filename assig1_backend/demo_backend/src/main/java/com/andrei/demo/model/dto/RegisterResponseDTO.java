package com.andrei.demo.model.dto;

public record RegisterResponseDTO(
    boolean success,
    String errorMessage
) {
}
