package com.github.tecnoguard.application.dtos.user.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.tecnoguard.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

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
        UserRole userRole,
        @Schema(description = "Se usuário está ativo", example = "true")
        Boolean active,
        @Schema(description = "Data de criação", example = "18/12/2025 09:30")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime createdAt,
        @Schema(description = "Data de atualização", example = "18/12/2025 09:30")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime updatedAt,
        @Schema(description = "Data do ultimo login", example = "18/12/2025 09:30")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime lastLogin
) {
}
