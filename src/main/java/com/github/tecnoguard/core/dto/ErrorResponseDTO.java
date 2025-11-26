package com.github.tecnoguard.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        @Schema(description = "Data e hora do Erro", example = "18/12/2025 09:30")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime timestamp,
        @Schema(description = "Tipo do Erro", example = "Erro X")
        String error,
        @Schema(description = "Detalhes do Erro", example = "Não foi possível fazer algo com X")
        String message,
        @Schema(description = "Caminho (URI) da requisição que falhou", example = "/api/user/xxx")
        String path
) {
}
