
package com.example.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.response.ErrorResponse;

import java.nio.file.AccessDeniedException;

@Slf4j
@ControllerAdvice
public class GlobalHandlerException {

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                        AccessDeniedException ex, HttpServletRequest request) {
                ErrorResponse errorResponse = buildErrorResponse(
                                HttpStatus.FORBIDDEN,
                                ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(errorResponse);
        }

        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<ErrorResponse> handleIllegalStateException(
                        IllegalStateException ex,
                        HttpServletRequest request) {
                log.warn("Warning occurred: {}", ex.getMessage(), ex);
                ErrorResponse errorResponse = buildErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(errorResponse);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
                        IllegalArgumentException ex,
                        HttpServletRequest request) {
                log.warn("Warning occurred: {}", ex.getMessage(), ex);
                ErrorResponse errorResponse = buildErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(errorResponse);
        }

        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex,
                        HttpServletRequest request) {
                String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();
                ErrorResponse errorResponse = buildErrorResponse(
                                ex.getStatusCode(),
                                message);
                return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                log.warn("Warning occurred: {}", ex.getMessage(), ex);
                String message = ex.getBindingResult().getAllErrors()
                                .stream()
                                .map(error -> error.getDefaultMessage())
                                .findFirst()
                                .orElse("Validation failed");
                ErrorResponse errorResponse = buildErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                message);
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(errorResponse);
        }

        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
                        HttpRequestMethodNotSupportedException ex,
                        HttpServletRequest request) {
                log.warn("Warning occurred: {}", ex.getMessage(), ex);
                ErrorResponse errorResponse = buildErrorResponse(
                                HttpStatus.METHOD_NOT_ALLOWED,
                                ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.METHOD_NOT_ALLOWED)
                                .body(errorResponse);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleMessageNotReadableException(
                        HttpMessageNotReadableException ex,
                        HttpServletRequest request) {
                log.warn("Warning occurred: {}", ex.getMessage(), ex);
                ErrorResponse errorResponse = buildErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(errorResponse);
        }

        @ExceptionHandler(UnsupportedOperationException.class)
        public ResponseEntity<ErrorResponse> handleUnsupportedOperationException(UnsupportedOperationException ex,
                        HttpServletRequest request) {
                log.warn("Warning occurred: {}", ex.getMessage(), ex);
                ErrorResponse errorResponse = buildErrorResponse(
                                HttpStatus.NOT_IMPLEMENTED,
                                ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.NOT_IMPLEMENTED)
                                .body(errorResponse);
        }

        // General handler for all unhandled exceptions
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, HttpServletRequest request) {
                log.error("Unhandled exception for request {} {}", request.getMethod(), request.getRequestURI(), ex);
                String msg = ex.getClass().getSimpleName() + ": " + (ex.getMessage() == null ? "" : ex.getMessage());
                ErrorResponse errorResponse = buildErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                msg);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

        private ErrorResponse buildErrorResponse(
                        HttpStatusCode status,
                        String message) {
                return new ErrorResponse(
                                message,
                                status.value());
        }

}
