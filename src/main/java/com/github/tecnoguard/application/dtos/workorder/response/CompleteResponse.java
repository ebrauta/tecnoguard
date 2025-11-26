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
public class CompleteResponse extends BaseWOResponse {
    @Schema(description = "Data de encerramento", example = "18/12/2025")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDateTime closingDate;
    @Schema(description = "tempo previsto (em horas)", example = "1.0")
    Double estimatedHours;
    @Schema(description = "custo previsto", example = "1.0")
    Double estimatedCost;
    @Schema(description = "tempo real gasto (em horas)", example = "1.0")
    Double actualHours;
    @Schema(description = "custo total", example = "1.0")
    Double actualCost;
}
