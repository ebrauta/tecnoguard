package com.github.tecnoguard.application.dtos.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDTO(
        @NotBlank(message = "Senha atual é obrigatória")
        @Size(min = 3, max = 72, message = "Senha deve ter no mínimo 3 caracteres")
        String currentPassword,
        @NotBlank(message = "Nova senha é obrigatória")
        @Size(min = 3, max = 72, message = "Senha deve ter no mínimo 3 caracteres")
        String newPassword
) {
}
