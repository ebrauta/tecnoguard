package com.github.tecnoguard.application.dtos.workorder.response;

import java.time.LocalDateTime;

public record WorkOrderNoteDTO(
        Long id,
        String message,
        String author,
        LocalDateTime createdAt
) {}
