package com.github.tecnoguard.application.dtos.workorder.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record WorkOrderNoteDTO(
        @Schema(description = "Identificação da nota", example = "1")
        Long id,
        @Schema(description = "Mensagem", example = "Nota atribuida ao Equipamento X")
        String message,
        @Schema(description = "Autor", example = "nome.sobrenome")
        String author,
        @Schema(description = "Data e hora de criação", example = "2025-11-15T09:00:00.00000000")
        LocalDateTime createdAt
) {
}
