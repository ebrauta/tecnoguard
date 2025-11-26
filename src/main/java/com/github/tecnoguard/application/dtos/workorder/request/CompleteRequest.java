package com.github.tecnoguard.application.dtos.workorder.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompleteRequest(
        @NotBlank(message = "Resumo do serviço é obrigatório")
        @Schema(description = "Resumo do serviço", example = "Serviço realizado com sucesso.")
        String log,
        @NotNull(message = "O tempo de trabalho é obrigatório")
        @DecimalMin(value = "0.0", message = "O tempo deve ser maior que 0h")
        @DecimalMax(value = "8760.0", message = "O tempo deve ser menor que 8760h (1 ano)")
        @Schema(description = "tempo previsto - calculado pela lista de tarefas", example = "1.0")
        Double actualHours,
        @NotNull(message = "O custo é obrigatório")
        @DecimalMin(value = "0.0", message = "O custo não pode ser negativo")
        @Schema(description = "custo previsto", example = "1.0")
        Double actualCost
) {
}
