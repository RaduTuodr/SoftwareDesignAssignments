package com.andrei.demo.model.dto;

import com.andrei.demo.validator.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfessorCreateDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name should be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Password is required")
    @StrongPassword(message = "Password must contain at least 8 characters, including uppercase, lowercase, digit, and special character")
    private String password;

    @NotNull(message = "Age is required")
    private Integer age;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    private String department;

    @Pattern(regexp = "^(Assistant Professor|Associate Professor|Professor|Distinguished Professor)$",
            message = "Academic rank must be one of: Assistant Professor, Associate Professor, " +
                    "Professor, Distinguished Professor")
    private String academicRank;
}
