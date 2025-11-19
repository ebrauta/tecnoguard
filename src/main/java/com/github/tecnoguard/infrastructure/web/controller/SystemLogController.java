package com.github.tecnoguard.infrastructure.web.controller;

import com.github.tecnoguard.application.dtos.auth.response.UserInfoDTO;
import com.github.tecnoguard.core.dto.ErrorResponse;
import com.github.tecnoguard.core.dto.PageDTO;
import com.github.tecnoguard.core.dto.SystemLogDTO;
import com.github.tecnoguard.core.models.SystemLog;
import com.github.tecnoguard.infrastructure.persistence.SystemLogRepository;
import com.github.tecnoguard.infrastructure.service.SystemLogServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SystemLog - Auditoria do Sistema", description = "Gestão de Auditoria do Sistema")
@RestController
@RequestMapping("/api/logs")
@PreAuthorize("hasRole('ADMIN')")
public class SystemLogController {

    public SystemLogController(SystemLogServiceImpl service) {
        this.service = service;
    }

    public static class SystemLogPageDTO extends PageDTO<SystemLogDTO> {
    }

    private final SystemLogServiceImpl service;


    @Operation(
            summary = "Listar log",
            description = "Mostra os logs do sistema.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SystemLogPageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Autenticação",
                                            summary = "Usuário não autenticado",
                                            value = """
                                                    {
                                                      "timestamp": "2025-11-19T15:00:00.00000000",
                                                      "error": "Erro de autenticação",
                                                      "message": "Usuário não autenticado",
                                                      "path": "/api/logs"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = org.springframework.web.ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Erro de Autorização",
                                            summary = "Usuário não autorizado",
                                            value = """
                                                    {
                                                      "timestamp": "2025-11-19T15:00:00.00000000",
                                                      "error": "Autorização",
                                                      "message": "Usuário não autorizado",
                                                      "path": "/api/logs"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<PageDTO<SystemLogDTO>> list(
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "timestamp", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<SystemLogDTO> page = service.list(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new PageDTO<>(page));
    }
}
