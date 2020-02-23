package com.dovydasvenckus.timetracker.core.rest.exception.validation;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class FieldValidationViolation {
    private String field;
    private String violation;
}
