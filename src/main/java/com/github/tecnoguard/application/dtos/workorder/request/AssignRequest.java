package com.github.tecnoguard.application.dtos.workorder.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.tecnoguard.core.utils.LocalDateTimeDeserializer;
import com.github.tecnoguard.domain.enums.WOMaintenanceTrigger;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record AssignRequest(
        @NotBlank(message = "Nome do técnico é obrigatório")
        @Schema(description = "Técnico agendado", example = "Nome do Técnico")
        String assignedTechnician,
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @NotNull(message = "Data de agendamento é obrigatória")
        @Schema(description = "Data de agendamento", example = "2025-11-15")
        LocalDateTime scheduledDate,
        @NotNull(message = "Uma previsão de tempo de trabalho é obrigatória")
        @DecimalMin(value = "0.0", message = "O tempo deve ser maior que 0h")
        @DecimalMax(value = "8760.0", message = "O tempo deve ser menor que 8760h (1 ano)")
        @Schema(description = "tempo previsto - calculado pela lista de tarefas", example = "1.0")
        Double estimatedHours,
        @NotNull(message = "Uma previsão de custo é obrigatória")
        @Min(value = 0, message = "O custo não pode ser negativo")
        @Schema(description = "custo previsto", example = "1.0")
        Double estimatedCost,
        Boolean requiresShutdown,
        Boolean safetyRisk,
        WOMaintenanceTrigger trigger
) {
}
