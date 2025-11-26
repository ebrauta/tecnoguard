package com.github.tecnoguard.application.dtos.auth.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record LoginResponseDTO(
        @Schema(description = "Nome do usuário para login", example = "nome.sobrenome")
        String username,
        @Schema(description = "Mensagem de autenticação", example = "Autenticação realizada com sucesso")
        String message,
        @Schema(description = "Token JWT", example = "sdf2v3980432dn98zs01n20928301ma3098201923280x0")
        String token,
        @Schema(description = "Data e hora de autenticação", example = "18/12/2025 09:30")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime timestamp
) {
}
