package com.github.tecnoguard.application.dtos.workorder.request;

import com.github.tecnoguard.domain.enums.WOPriority;
import com.github.tecnoguard.domain.enums.WOType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRequest(
        @NotBlank(message = "Descrição é obrigatória")
        @Schema(description = "Descrição do Serviço", example = "Troca de óleo")
        String description,
        @NotBlank(message = "Equipamento é obrigatório")
        @Schema(description = "Nome do equipamento", example = "Bomba A")
        String equipment,
        @NotBlank(message = "Cliente é obrigatório")
        @Schema(description = "Cliente onde o serviço é executado", example = "Cliente X")
        String client,
        @NotNull(message = "Tipo é obrigatório")
        @Schema(description = "Tipo de manutenção", example = "CORRECTIVE")
        WOType type,
        @NotNull(message = "Prioridade é obrigatória")
        @Schema(description = "Prioridade do serviço", example = "MEDIUM")
        WOPriority priority
) {
}
