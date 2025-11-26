package com.github.tecnoguard.application.dtos.user.request;

import com.github.tecnoguard.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;

public record CreateUserDTO(
        @NotBlank(message = "Nome do usuário é obrigatório")
        @Size(min = 3, message = "O nome do usuário deve ter no mínimo 3 caracteres")
        @Schema(description = "Nome do Usuário", example = "Nome Sobrenome")
        String name,
        @NotBlank(message = "Usuário é obrigatório")
        @Schema(description = "Usuário", example = "nome.sobrenome")
        String username,
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 3, max = 72, message = "Senha deve ter no mínimo 3 caracteres")
        @Schema(description = "Senha", example = "123456")
        String password,
        @NotNull(message = "Regra é obrigatória")
        @Enumerated(EnumType.STRING)
        @Schema(description = "Permissão do Usuário", example = "TECHNICIAN")
        UserRole role,
        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail é obrigatório")
        @Schema(description = "E-mail do Usuário", example = "usuario@mail.com")
        String email
) {
}
