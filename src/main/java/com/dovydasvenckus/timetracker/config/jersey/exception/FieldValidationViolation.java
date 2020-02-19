package com.dovydasvenckus.timetracker.config.jersey.exception;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class FieldValidationViolation {
    private String field;
    private String violation;
}
