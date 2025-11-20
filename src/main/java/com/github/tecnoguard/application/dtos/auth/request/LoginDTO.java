package com.github.tecnoguard.application.dtos.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank(message = "Usuário é obrigatório")
        @Schema(description = "Nome do usuário para login", example = "nome.sobrenome")
        String username,
        @NotBlank(message = "Senha é obrigatória")
        @Schema(description = "Senha do usuário", example = "Senha123")
        String password
) {
}
