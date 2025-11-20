package com.github.tecnoguard.application.dtos.workorder.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CompleteRequest(
        @NotBlank(message = "Resumo do serviço é obrigatório")
        @Schema(description = "Resumo do serviço", example = "Serviço realizado com sucesso.")
        String log
) {
}
