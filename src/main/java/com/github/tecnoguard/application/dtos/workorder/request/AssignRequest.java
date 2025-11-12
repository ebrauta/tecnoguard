package com.github.tecnoguard.application.dtos.workorder.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AssignRequest(
        @NotBlank(message = "Nome do técnico é obrigatório") String tech,
        @NotNull(message = "Data de agendamento é obrigatória") LocalDate date
) {
}
