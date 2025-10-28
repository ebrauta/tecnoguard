package com.github.tecnoguard.application.dtos.user.request;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank String username,
        @NotBlank String password
) {
}
