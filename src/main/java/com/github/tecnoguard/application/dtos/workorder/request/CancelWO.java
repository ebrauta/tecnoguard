package com.github.tecnoguard.application.dtos.workorder.request;

import jakarta.validation.constraints.NotBlank;

public record CancelWO(
        @NotBlank(message = "Motivo do cancelamento é obrigatório") String reason
) {
}
