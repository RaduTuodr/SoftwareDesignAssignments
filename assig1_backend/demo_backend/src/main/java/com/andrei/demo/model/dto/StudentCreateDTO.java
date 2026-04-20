package com.andrei.demo.model.dto;

import com.andrei.demo.validator.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentCreateDTO {

    @Size(min = 2, max = 100, message = "Name should be between 2 and 100 characters")
    private String name;

    @StrongPassword(message = "Password must contain at least 8 characters, including uppercase, lowercase, digit, and special character")
    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Age is required")
    private Integer age;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    private String registrationNumber; 

    private Integer graduationYear;
}
