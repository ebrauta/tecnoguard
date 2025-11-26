package com.github.tecnoguard.application.dtos.user.request;

import com.github.tecnoguard.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserDTO(
        @Schema(description = "Nome atualizado", example = "Nome Novo Sobrenome")
        @Size(min = 3, message = "O nome do usuário deve ter no mínimo 3 caracteres")
        String name,
        @Email(message = "E-mail inválido")
        @Schema(description = "E-mail atualizado", example = "novoemail@mail.com")
        String email,
        @Schema(description = "Permissão atualizada", example = "OPERATOR")
        UserRole role) {
}
