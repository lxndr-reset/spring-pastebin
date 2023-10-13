package com.pastebin.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.Email;

public class EmailValidator implements ConstraintValidator<Email, String> {
    @Override
    public void initialize(Email constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s != null && !s.isEmpty() && s.matches("^[a-zA-Z0-9._+-]+@$[a-zA-Z0-9-._]+.[a-zA-Z]{2,}");

    }
}
