package com.github.tecnoguard.core.shared;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        String error,
        String message,
        String path
) {
}
