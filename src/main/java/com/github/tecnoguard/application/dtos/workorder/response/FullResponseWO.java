package com.github.tecnoguard.application.dtos.workorder.response;

import com.github.tecnoguard.domain.enums.WOStatus;
import com.github.tecnoguard.domain.enums.WOType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record FullResponseWO(
        Long id,
        String description,
        String equipment,
        String client,
        String assignedTechnician,
        LocalDate scheduledDate,
        WOType type,
        WOStatus status,
        String cancelReason,
        LocalDateTime completedAt
) {
}
