package com.dovydasvenckus.timetracker.core.rest.exception.validation;


import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        try {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(objectMapper.writeValueAsString(extractViolations(exception)))
                    .type("application/json")
                    .build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private FieldViolations extractViolations(ConstraintViolationException exception) {
        List<FieldValidationViolation> violations = exception.getConstraintViolations().stream()
                .map(cv -> new FieldValidationViolation(extractViolationName(cv), cv.getMessage()))
                .collect(Collectors.toList());

        return new FieldViolations(violations);
    }

    private String extractViolationName(ConstraintViolation<?> cv) {
        return StreamSupport.stream(cv.getPropertyPath().spliterator(), false)
                .reduce((a, b) -> b)
                .map(Path.Node::getName)
                .orElse(null);
    }
}
