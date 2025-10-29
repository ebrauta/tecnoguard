package com.github.tecnoguard.application.dtos.user.request;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank(message = "Usuário é obrigatório")
        String username,
        @NotBlank(message = "Senha é obrigatória")
        String password
) {
}
