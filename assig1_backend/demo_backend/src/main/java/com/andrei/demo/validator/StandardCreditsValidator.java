package com.andrei.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StandardCreditsValidator implements ConstraintValidator<StandardCredits, Integer> {

    @Override
    public void initialize(StandardCredits constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer credits, ConstraintValidatorContext constraintValidatorContext) {
        if (credits == null) { return false; }

        return credits >= 3 && credits <= 12;
    }
}
