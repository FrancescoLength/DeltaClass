package com.fulfilment.application.monolith.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Global exception mapper for {@link ValidationException}.
 * Maps business validation errors to HTTP 400 (Bad Request) responses,
 * ensuring that domain-level validation failures are communicated as
 * client errors rather than server errors.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    private static final Logger LOGGER = Logger.getLogger(ValidationExceptionMapper.class);

    @Inject
    ObjectMapper objectMapper;

    @Override
    public Response toResponse(ValidationException exception) {
        LOGGER.warnf("Validation failed: %s", exception.getMessage());

        ObjectNode errorJson = objectMapper.createObjectNode();
        errorJson.put("exceptionType", exception.getClass().getName());
        errorJson.put("code", 400);
        errorJson.put("error", exception.getMessage());

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorJson)
                .build();
    }
}
