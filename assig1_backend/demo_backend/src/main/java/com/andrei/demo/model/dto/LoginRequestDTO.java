package com.andrei.demo.model.dto;

public record LoginRequestDTO(
        String email,
        String password
) {
}