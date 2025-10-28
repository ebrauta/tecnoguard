package com.github.tecnoguard.application.dtos.user.response;

import java.time.LocalDateTime;

public record RegisterReponseDTO(
        Long id,
        String username,
        String role,
        LocalDateTime createdAt
) {
}
