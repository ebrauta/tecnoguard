package com.github.tecnoguard.application.dtos.user.response;

import com.github.tecnoguard.domain.enums.UserRole;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String username,
        String name,
        String email,
        UserRole role,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastLogin
) {
}
