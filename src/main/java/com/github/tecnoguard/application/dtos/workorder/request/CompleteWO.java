package com.github.tecnoguard.application.dtos.workorder.request;

import jakarta.validation.constraints.NotBlank;

public record CompleteWO(
        @NotBlank(message = "Resumo do serviço é obrigatório") String log
) {
}
