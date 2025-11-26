package com.github.tecnoguard.application.dtos.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserInfoDTO(
        @Schema(description = "Nome do usuário para login", example = "Nome Sobrenome")
        String name,
        @Schema(description = "Nome do usuário para login", example = "nome.sobrenome")
        String username,
        @Schema(description = "Regra de permissão do usuário", example = "ROLE_OPERATOR")
        String role
) {
}
