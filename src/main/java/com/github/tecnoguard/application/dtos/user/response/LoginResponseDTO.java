package com.github.tecnoguard.application.dtos.user.response;

import com.github.tecnoguard.domain.enums.UserRole;

import java.time.LocalDateTime;

public record LoginResponseDTO(
        String username,
        String message,
        LocalDateTime timestamp
) {
}
