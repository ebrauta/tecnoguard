package com.github.tecnoguard.infrastructure.web.controller;

import com.github.tecnoguard.application.dtos.workorder.request.*;
import com.github.tecnoguard.application.dtos.workorder.response.*;
import com.github.tecnoguard.application.mappers.workorder.WorkOrderMapper;
import com.github.tecnoguard.application.mappers.workorder.WorkOrderNoteMapper;
import com.github.tecnoguard.core.dto.ErrorResponseDTO;
import com.github.tecnoguard.core.dto.PageDTO;
import com.github.tecnoguard.domain.models.WorkOrder;
import com.github.tecnoguard.domain.models.WorkOrderNote;
import com.github.tecnoguard.domain.service.IWorkOrderNoteService;
import com.github.tecnoguard.domain.service.IWorkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Work Orders - Ordens de Serviço", description = "Gestão do ciclo de vida das Ordens de Serviço")
@RestController
@RequestMapping("/api/workorders")
public class WorkOrderController {

    public static class GenericPageDTO extends PageDTO<Object> {
    }

    public static class NotePageDTO extends PageDTO<WorkOrderNoteDTO> {
    }

    private final IWorkService service;
    private final IWorkOrderNoteService noteService;
    private final WorkOrderMapper mapper;
    private final WorkOrderNoteMapper noteMapper;

    public WorkOrderController(IWorkService service, IWorkOrderNoteService noteService, WorkOrderMapper mapper, WorkOrderNoteMapper noteMapper) {
        this.service = service;
        this.noteService = noteService;
        this.mapper = mapper;
        this.noteMapper = noteMapper;
    }

    private Object typeResponse(WorkOrder wo) {
        return switch (wo.getStatus()) {
            case OPEN -> mapper.entityToCreateResponse(wo);
            case SCHEDULED -> mapper.entityToAssignResponse(wo);
            case IN_PROGRESS -> mapper.entityToStartResponse(wo);
            case COMPLETED -> mapper.entityToCompleteResponse(wo);
            case CANCELLED -> mapper.entityToCancelResponse(wo);
            default -> null;
        };
    }

    @Operation(
            summary = "Listar todas",
            description = "Lista todas as OS.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = GenericPageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    ),
            }
    )
    @GetMapping
    public ResponseEntity<PageDTO<?>> list(
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "equipment", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<?> page = service.list(pageable).map(this::typeResponse);
        return ResponseEntity.status(HttpStatus.OK).body(new PageDTO<>(page));
    }

    @Operation(
            summary = "Listar Log da OS",
            description = "Mostrar documentação da OS.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotePageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ordem de Serviço Não Encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "404", ref = "404")
                            )
                    )
            }
    )
    @GetMapping("/log/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'PLANNER')")
    public ResponseEntity<PageDTO<WorkOrderNoteDTO>> getNotes(
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            @PathVariable Long id
    ) {
        WorkOrder wo = service.findById(id);
        Page<WorkOrderNoteDTO> page = noteService.listNotes(wo.getId(), pageable)
                .map(note -> new WorkOrderNoteDTO(note.getId(), note.getMessage(), note.getAuthor(), note.getCreatedAt()));
        return ResponseEntity.status(HttpStatus.OK).body(new PageDTO<>(page));
    }

    @Operation(
            summary = "Adicionar Log da OS",
            description = "Mostrar documentação da OS.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Nota adicionada com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WorkOrderNoteDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ordem de Serviço Não Encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "404", ref = "404")
                            )
                    )
            }
    )
    @PostMapping("/log/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'PLANNER', 'TECHNICIAN')")
    public ResponseEntity<WorkOrderNoteDTO> addLog(
            @Valid
            @PathVariable Long id,
            @RequestBody AddNoteWO noteWO
    ) {
        WorkOrder wo = service.findById(id);
        String author = SecurityContextHolder.getContext().getAuthentication().getName();
        WorkOrderNote note = noteService.addNote(wo, noteWO.message(), author);
        WorkOrderNoteDTO response = noteMapper.toDTO(note);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Detalhar OS",
            description = "Mostra detalhes da OS.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ordem de Serviço Não Encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "404", ref = "404")
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> get(
            @Valid
            @PathVariable Long id) {
        WorkOrder wo = service.findById(id);
        Object responseBody = typeResponse(wo);
        if (responseBody != null) {
            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Criar nova OS",
            description = "Cria uma nova OS.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Ordem de Serviço criada com Sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PLANNER', 'OPERATOR')")
    public ResponseEntity<CreateResponse> create(
            @Valid
            @RequestBody CreateRequest dto) {
        WorkOrder wo = mapper.createRequestToEntity(dto);
        WorkOrder created = service.create(wo);
        CreateResponse response = mapper.entityToCreateResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Agendar OS",
            description = "Faz o agendamento de uma OS.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Os agendada com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AssignResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Quebra de Regra de Negócio",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Erro: Regra de Negócio",
                                            summary = "Tipo de Usuário sem Permissão.",
                                            value = """
                                                    {
                                                      "timestamp": "2025-11-19T15:00:00.000",
                                                      "error": "Regra de Negócio",
                                                      "message": "Usuário não tem permissão para agendar esta OS.",
                                                      "path": "{URL_DA_REQUISIÇÃO}
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ordem de Serviço Não Encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "404", ref = "404")
                            )
                    )
            }
    )
    @PatchMapping("/assign/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLANNER')")
    public ResponseEntity<AssignResponse> assign(
            @Valid
            @PathVariable Long id,
            @RequestBody AssignRequest dto) {

        WorkOrder assigned = service.assign(id, dto.tech(), dto.date());
        AssignResponse response = mapper.entityToAssignResponse(assigned);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Iniciar Serviço",
            description = "Inicia o serviço de uma OS.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Os iniciada com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StartResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Quebra de Regra de Negócio",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Erro: Regra de Negócio",
                                            summary = "Tipo de Usuário sem Permissão.",
                                            value = """
                                                    {
                                                      "timestamp": "2025-11-19T15:00:00.000",
                                                      "error": "Regra de Negócio",
                                                      "message": "Usuário não tem permissão para iniciar esta OS.",
                                                      "path": "{URL_DA_REQUISIÇÃO}"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ordem de Serviço Não Encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "404", ref = "404")
                            )
                    )
            }
    )
    @PatchMapping("/start/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<StartResponse> start(
            @Valid
            @PathVariable Long id) {
        WorkOrder started = service.start(id);
        StartResponse response = mapper.entityToStartResponse(started);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Terminar Serviço",
            description = "Finaliza o serviço de uma OS.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Os finalizada com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CompleteResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Quebra de Regra de Negócio",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Erro: Regra de Negócio",
                                            summary = "Tipo de Usuário sem Permissão.",
                                            value = """
                                                    {
                                                      "timestamp": "2025-11-19T15:00:00.000",
                                                      "error": "Regra de Negócio",
                                                      "message": "Usuário não tem permissão para finalizar esta OS.",
                                                      "path": "{URL_DA_REQUISIÇÃO}"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ordem de Serviço Não Encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "404", ref = "404")
                            )
                    )
            }
    )
    @PatchMapping("/complete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'TECHNICIAN')")
    public ResponseEntity<CompleteResponse> complete(
            @Valid
            @PathVariable Long id,
            @RequestBody CompleteRequest dto) {
        WorkOrder completed = service.complete(id, dto.log());
        CompleteResponse response = mapper.entityToCompleteResponse(completed);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Cancelar Serviço",
            description = "Cancela uma OS.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Os cancelada com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CancelResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Quebra de Regra de Negócio",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Erro: Regra de Negócio",
                                            summary = "Tipo de Usuário sem Permissão.",
                                            value = """
                                                    {
                                                      "timestamp": "2025-11-19T15:00:00.000",
                                                      "error": "Regra de Negócio",
                                                      "message": "Usuário não tem permissão para cancelar esta OS.",
                                                      "path": "{URL_DA_REQUISIÇÃO}"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuário Não Autenticado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "401", ref = "401")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso Não Permitido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "403", ref = "403")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ordem de Serviço Não Encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(name = "404", ref = "404")
                            )
                    )
            }
    )
    @PatchMapping("/cancel/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<CancelResponse> cancel(
            @Valid
            @PathVariable Long id,
            @RequestBody CancelRequest dto) {
        WorkOrder cancelled = service.cancel(id, dto.reason());
        CancelResponse response = mapper.entityToCancelResponse(cancelled);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
