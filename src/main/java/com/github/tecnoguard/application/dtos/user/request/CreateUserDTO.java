package com.github.tecnoguard.application.dtos.user.request;

import com.github.tecnoguard.domain.enums.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;

public record CreateUserDTO(
        @NotBlank(message = "Nome do usuário é obrigatório")
        String name,
        @NotBlank(message = "Usuário é obrigatório")
        String username,
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 3, max = 72, message = "Senha deve ter no mínimo 3 caracteres")
        String password,
        @NotNull(message = "Regra é obrigatória")
        @Enumerated(EnumType.STRING)
        UserRole role,
        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail é obrigatório")
        String email
) {
}
