package com.github.tecnoguard.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        @Schema(description = "Data e hora do Erro", example = "2025-11-15T15:00:00.000000000")
        LocalDateTime timestamp,
        @Schema(description = "Tipo do Erro", example = "Erro X")
        String error,
        @Schema(description = "Detalhes do Erro", example = "Não foi possível fazer algo com X")
        String message,
        @Schema(description = "Caminho (URI) da requisição que falhou", example = "/api/user/111")
        String path
) {
}
