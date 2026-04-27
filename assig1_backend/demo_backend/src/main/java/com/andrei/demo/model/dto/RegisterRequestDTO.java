package com.andrei.demo.model.dto;

import com.andrei.demo.model.enums.RoleName;

public record RegisterRequestDTO (
    String name,
    Integer age,
    String email,
    String password,
    RoleName roleName
) {
}
