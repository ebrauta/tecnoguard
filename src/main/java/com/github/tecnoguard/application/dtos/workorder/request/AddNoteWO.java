package com.github.tecnoguard.application.dtos.workorder.request;

import jakarta.validation.constraints.NotNull;

public record AddNoteWO(
        @NotNull(message = "A mensagem não pode estar vazia")
        String message) {
}
