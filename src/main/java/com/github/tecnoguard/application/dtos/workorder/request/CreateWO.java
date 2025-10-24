package com.github.tecnoguard.application.dtos.workorder.request;

import com.github.tecnoguard.domain.enums.WOType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateWO(
        @NotBlank(message = "Descrição é obrigatória") String description,
        @NotBlank(message = "Equipamento é obrigatório") String equipment,
        @NotBlank(message = "Cliente é obrigatório") String client,
        @NotNull(message = "Tipo é obrigatório") WOType type
) {
}
