package com.dovydasvenckus.timetracker.core.rest.exception.validation;

import lombok.AllArgsConstructor;
import lombok.Value;

import javax.ws.rs.core.Response;
import java.util.List;

@Value
@AllArgsConstructor
public class FieldViolations {
    private List<FieldValidationViolation> validationViolations;
    private final int status = Response.Status.BAD_REQUEST.getStatusCode();
}
