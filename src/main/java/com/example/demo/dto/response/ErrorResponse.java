package com.example.demo.dto.response;

/**
 * Represents an error response with a message and HTTP status code.
 *
 * @param message the error message
 * @param status the HTTP status code associated with the error
 */
public record ErrorResponse(
        String message,
        int status) {
}

