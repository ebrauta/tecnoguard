package com.github.tecnoguard.application.dtos.workorder.response;

import com.github.tecnoguard.domain.enums.WOPriority;
import com.github.tecnoguard.domain.enums.WOStatus;
import com.github.tecnoguard.domain.enums.WOType;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateResponse(
        @Schema(description = "Identificação do serviço", example = "1")
        Long id,
        @Schema(description = "Descrição do Serviço", example = "Troca de óleo")
        String description,
        @Schema(description = "Nome do equipamento", example = "Bomba A")
        String equipment,
        @Schema(description = "Cliente onde o serviço é executado", example = "Cliente X")
        String client,
        @Schema(description = "Tipo de manutenção", example = "CORRETIVE")
        WOType type,
        @Schema(description = "Prioridade do serviço", example = "MEDIUM")
        WOPriority priority,
        @Schema(description = "Estado do serviço", example = "SCHEDULED")
        WOStatus status
) {
}
