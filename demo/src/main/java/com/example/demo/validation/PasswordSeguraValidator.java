package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class PasswordSeguraValidator implements ConstraintValidator<PasswordSegura, String> {

    private static final int MIN = 8;
    private static final int MAX = 30;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return true;
        }

        List<String> errores = new ArrayList<>();

        if (password.length() < MIN || password.length() > MAX) {
            errores.add("Debe tener entre 8 y 30 caracteres");
        }
        if (password.chars().noneMatch(Character::isUpperCase)) {
            errores.add("Debe incluir al menos una mayúscula");
        }
        if (password.chars().noneMatch(Character::isDigit)) {
            errores.add("Debe incluir al menos un número");
        }
        if (password.chars().noneMatch(c -> !Character.isLetterOrDigit(c) && !Character.isWhitespace(c))) {
            errores.add("Debe incluir al menos un carácter especial");
        }

        if (errores.isEmpty()) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        errores.forEach(e -> context.buildConstraintViolationWithTemplate(e).addConstraintViolation());
        return false;
    }
}