package com.github.tecnoguard.core.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        String error,
        String message,
        String path
) {
}
