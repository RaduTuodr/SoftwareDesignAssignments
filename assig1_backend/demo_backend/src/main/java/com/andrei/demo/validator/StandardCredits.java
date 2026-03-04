package com.andrei.demo.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StandardCreditsValidator.class)
public @interface StandardCredits {
    String message() default "No. credits must be between 3 and 12";

    /**
     * One could use groups to categorize constrains, like Create vs Update.
     */
    Class<?>[] groups() default {};

    /**
     * One could use metadata payload to attach additional information to a validation failure.
     * This can be used to associate severity levels and error codes.
     */
    Class<? extends Payload>[] payload() default {};
}
