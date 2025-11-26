package com.github.tecnoguard.application.dtos.workorder.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.tecnoguard.core.utils.LocalDateTimeDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AssignRequest(
        @NotBlank(message = "Nome do técnico é obrigatório")
        @Schema(description = "Técnico agendado", example = "Nome do Técnico")
        String assignedTechnician,
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @NotNull(message = "Data de agendamento é obrigatória")
        @Schema(description = "Data de agendamento", example = "2025-11-15")
        LocalDateTime scheduledDate
) {
}
