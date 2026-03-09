package com.andrei.demo.model;

import com.andrei.demo.validator.StandardCredits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourseCreateDTO {

    @NotBlank(message = "Course title is required")
    @Size(min = 2, max = 50, message = "Course title should be between 2 and 50 characters")
    private String title;

    private String description;

    @NotNull(message = "Number of credits is required")
    @StandardCredits
    private Integer credits;
}
