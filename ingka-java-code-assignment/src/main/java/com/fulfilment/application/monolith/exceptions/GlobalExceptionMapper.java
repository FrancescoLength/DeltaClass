package com.fulfilment.application.monolith.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Global exception mapper for the application.
 * Centralizes error handling by mapping various exceptions to appropriate HTTP
 * status codes
 * and providing a consistent JSON error response structure.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class);

    @Inject
    ObjectMapper objectMapper;

    @Override
    public Response toResponse(Exception exception) {
        int code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

        if (exception instanceof WebApplicationException) {
            code = ((WebApplicationException) exception).getResponse().getStatus();
        } else if (exception instanceof ValidationException) {
            code = Response.Status.BAD_REQUEST.getStatusCode();
        }

        if (code >= 500) {
            LOGGER.error("Internal Server Error", exception);
        } else {
            LOGGER.warnf("Client Error (%d): %s", code, exception.getMessage());
        }

        ObjectNode errorJson = objectMapper.createObjectNode();
        errorJson.put("exceptionType", exception.getClass().getName());
        errorJson.put("code", code);

        if (exception.getMessage() != null) {
            errorJson.put("error", exception.getMessage());
        }

        return Response.status(code)
                .entity(errorJson)
                .build();
    }
}
