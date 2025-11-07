package com.github.tecnoguard.application.dtos.user.request;

import com.github.tecnoguard.domain.enums.UserRole;
import jakarta.validation.constraints.Email;

public record UpdateUserDTO(
        String name,
        @Email(message = "E-mail inválido")
        String email,
        UserRole role) {
}
