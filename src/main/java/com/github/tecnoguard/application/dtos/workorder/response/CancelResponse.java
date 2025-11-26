package com.github.tecnoguard.application.dtos.workorder.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CancelResponse extends BaseWOResponse {
    @Schema(description = "Motivo do cancelamento", example = "Máquina vendida.")
    String cancelReason;
    @Schema(description = "Data de Cancelamento", example = "18/12/2025")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDateTime cancelDate;
}
