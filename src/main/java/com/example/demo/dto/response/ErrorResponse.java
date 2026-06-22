package com.example.demo.dto.response;

public record ErrorResponse(
        String message,
        int status) {
}
