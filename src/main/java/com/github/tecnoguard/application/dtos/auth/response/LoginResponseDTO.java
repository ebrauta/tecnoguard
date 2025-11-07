package com.github.tecnoguard.application.dtos.auth.response;

import java.time.LocalDateTime;

public record LoginResponseDTO(
        String username,
        String message,
        String token,
        LocalDateTime timestamp
) {
}
