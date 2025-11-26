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
public class StartResponse extends BaseWOResponse {
    @Schema(description = "Data de abertura", example = "18/12/2025")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDateTime startDate;
}