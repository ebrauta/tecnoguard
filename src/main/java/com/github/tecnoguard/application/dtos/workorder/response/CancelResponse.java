package com.github.tecnoguard.application.dtos.workorder.response;

import com.github.tecnoguard.domain.enums.WOStatus;
import com.github.tecnoguard.domain.enums.WOType;

import java.time.LocalDateTime;

public record CancelResponse(
        Long id,
        String description,
        String equipment,
        String client,
        WOType type,
        String cancelReason,
        WOStatus status
) {
}
