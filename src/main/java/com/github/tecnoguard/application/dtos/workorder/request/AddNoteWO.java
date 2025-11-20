package com.github.tecnoguard.application.dtos.workorder.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AddNoteWO(
        @NotNull(message = "A mensagem não pode estar vazia")
        @Schema(description = "Mensagem a ser adicionada", example = "Terminado o serviço.")
        String message) {
}
