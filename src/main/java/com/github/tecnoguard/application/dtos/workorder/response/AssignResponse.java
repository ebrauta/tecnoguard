package com.github.tecnoguard.application.dtos.workorder.response;

import com.github.tecnoguard.domain.enums.WOPriority;
import com.github.tecnoguard.domain.enums.WOStatus;
import com.github.tecnoguard.domain.enums.WOType;

import java.time.LocalDate;

public record AssignResponse(
        Long id,
        String description,
        String equipment,
        String client,
        WOType type,
        WOPriority priority,
        String assignedTechnician,
        LocalDate scheduledDate,
        WOStatus status
) {
}
