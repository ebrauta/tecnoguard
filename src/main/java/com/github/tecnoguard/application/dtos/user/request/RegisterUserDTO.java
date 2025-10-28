package com.github.tecnoguard.application.dtos.user.request;

import com.github.tecnoguard.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserDTO(
        @NotBlank String username,
        @NotBlank @Min(3) String password,
        UserRole role,
        @Email @NotBlank String email
) {
}
