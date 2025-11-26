package com.github.tecnoguard.application.dtos.workorder.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CancelRequest(
        @NotBlank(message = "Motivo do cancelamento é obrigatório")
        @Schema(description = "Motivo do cancelamento", example = "Máquina vendida.")
        String cancelReason
) {
}
