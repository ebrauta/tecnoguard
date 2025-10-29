package com.github.tecnoguard.application.dtos.user.request;

import com.github.tecnoguard.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterUserDTO(
        @NotBlank(message = "Usuário é obrigatório")
        String username,
        @NotBlank(message = "Senha é obrigatória")
        @Min(value = 3, message = "Senha deve ter no mínimo 3 caracteres")
        String password,
        @NotNull(message = "Regra é obrigatória")
        UserRole role,
        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail é obrigatório")
        String email
) {
}
