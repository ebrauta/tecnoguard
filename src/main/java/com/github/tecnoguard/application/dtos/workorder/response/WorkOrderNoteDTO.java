package com.github.tecnoguard.application.dtos.workorder.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record WorkOrderNoteDTO(
        @Schema(description = "Identificação da nota", example = "1")
        Long id,
        @Schema(description = "Mensagem", example = "Nota atribuída ao Equipamento X")
        String message,
        @Schema(description = "Autor", example = "nome.sobrenome")
        String author,
        @Schema(description = "Data e hora de criação", example = "18/12/2025 09:30")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime createdAt
) {
}
