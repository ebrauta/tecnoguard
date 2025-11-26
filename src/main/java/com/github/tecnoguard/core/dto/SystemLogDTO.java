package com.github.tecnoguard.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record SystemLogDTO(
        @Schema(description = "Identificação do Log", example = "1")
        Long id,
        @Schema(description = "Data e hora da ação", example = "18/12/2025 09:30")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime timestamp,
        @Schema(description = "Usuário que praticou a ação", example = "nome.sobrenome")
        String actorUsername,
        @Schema(description = "Ação", example = "TARGET_CREATED")
        String action,
        @Schema(description = "Entidade da Ação", example = "TARGET")
        String targetType,
        @Schema(description = "Id da Entidade da Ação", example = "1")
        Long targetId,
        @Schema(description = "Mais detalhes da Ação", example = "O TARGET foi criado pelo usuário X. ")
        String details) {
}
