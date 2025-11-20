package com.github.tecnoguard.application.dtos.user.response;

import com.github.tecnoguard.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UserResponseDTO(
        @Schema(description = "Identificação do Usuário", example = "1")
        Long id,
        @Schema(description = "Usuário", example = "nome.sobrenome")
        String username,
        @Schema(description = "Nome", example = "Nome Sobrenome")
        String name,
        @Schema(description = "E-mail", example = "nome@mail.com")
        String email,
        @Schema(description = "Permissão", example = "OPERATOR")
        UserRole role,
        @Schema(description = "Se usuário está ativo", example = "true")
        Boolean active,
        @Schema(description = "Data de criação", example = "2025-11-15T05:00:00.0000000000")
        LocalDateTime createdAt,
        @Schema(description = "Data de atualização", example = "2025-11-15T15:00:00.0000000000")
        LocalDateTime updatedAt,
        @Schema(description = "Data do ultimo login", example = "2025-11-16T15:00:00.0000000000")
        LocalDateTime lastLogin
) {
}
