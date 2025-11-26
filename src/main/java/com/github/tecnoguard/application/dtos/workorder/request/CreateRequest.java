package com.github.tecnoguard.application.dtos.workorder.request;

import com.github.tecnoguard.domain.enums.WOPriority;
import com.github.tecnoguard.domain.enums.WOType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

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
        WOPriority priority,
        @NotNull(message = "Uma previsão de tempo de trabalho é obrigatória")
        @DecimalMin(value = "0.0", message = "O tempo deve ser maior que 0h")
        @DecimalMax(value = "8760.0", message = "O tempo deve ser menor que 8760h (1 ano)")
        @Schema(description = "tempo previsto - calculado pela lista de tarefas", example = "1.0")
        Double estimatedHours,
        @NotNull(message = "Uma previsão de custo é obrigatória")
        @Min(value = 0, message = "O custo não pode ser negativo")
        @Schema(description = "custo previsto", example = "1.0")
        Double estimatedCost
) {
}
