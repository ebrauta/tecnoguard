package com.github.tecnoguard.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record SystemLogDTO(
        @Schema(description = "Identificação do Log", example = "1")
        Long id,
        @Schema(description = "Data e hora da ação", example = "2025-11-19T15:00:00.00000000")
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
