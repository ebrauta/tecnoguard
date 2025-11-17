package com.github.tecnoguard.infrastructure.web.controller;

import com.github.tecnoguard.application.dtos.workorder.request.*;
import com.github.tecnoguard.application.dtos.workorder.response.*;
import com.github.tecnoguard.application.mappers.workorder.WorkOrderMapper;
import com.github.tecnoguard.application.mappers.workorder.WorkOrderNoteMapper;
import com.github.tecnoguard.core.dto.PageDTO;
import com.github.tecnoguard.domain.models.WorkOrder;
import com.github.tecnoguard.domain.models.WorkOrderNote;
import com.github.tecnoguard.domain.service.IWorkOrderNoteService;
import com.github.tecnoguard.domain.service.IWorkService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "Work Orders - Ordens de Serviço", description = "Gestão do ciclo de vida das Ordens de Serviço")
@RestController
@RequestMapping("/api/workorders")
public class WorkOrderController {

    private final IWorkService service;
    private final IWorkOrderNoteService noteService;
    private final WorkOrderMapper mapper;
    private final WorkOrderNoteMapper noteMapper = new WorkOrderNoteMapper();

    public WorkOrderController(IWorkService service, IWorkOrderNoteService noteService, WorkOrderMapper mapper) {
        this.service = service;
        this.noteService = noteService;
        this.mapper = mapper;
    }

    private Object typeResponse(WorkOrder wo){
        return switch (wo.getStatus()) {
            case OPEN -> mapper.entityToCreateResponse(wo);
            case SCHEDULED -> mapper.entityToAssignResponse(wo);
            case IN_PROGRESS -> mapper.entityToStartResponse(wo);
            case COMPLETED -> mapper.entityToCompleteResponse(wo);
            case CANCELLED -> mapper.entityToCancelResponse(wo);
            default -> null;
        };
    }

    @Operation(summary = "Listar todas", description = "Lista todas as OS.")
    @GetMapping
    public ResponseEntity<PageDTO<?>> list(
            @Valid
            @PageableDefault(size = 10, sort = "equipment", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<?> page = service.list(pageable).map(this::typeResponse);
        return ResponseEntity.status(HttpStatus.OK).body(new PageDTO<>(page));
    }

    @Operation(summary = "Listar Log da OS", description = "Mostrar documentação da OS.")
    @GetMapping("/log/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'PLANNER')")
    public ResponseEntity<PageDTO<WorkOrderNoteDTO>> getNotes(
            @Valid
            @PageableDefault(size = 10)
            Pageable pageable,
            @PathVariable Long id
    ) {
        WorkOrder wo = service.findById(id);
        Page<WorkOrderNoteDTO> page = noteService.listNotes(wo.getId(), pageable)
                .map(note -> new WorkOrderNoteDTO(note.getId(), note.getMessage(), note.getAuthor(), note.getCreatedAt()));
        return ResponseEntity.status(HttpStatus.OK).body(new PageDTO<>(page));
    }

    @Operation(summary = "Adicionar Log da OS", description = "Mostrar documentação da OS.")
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

    @Operation(summary = "Detalhar OS", description = "Mostra detalhes da OS.\nCampo obrigatório: id")
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

    @Operation(summary = "Criar nova OS", description = "Cria uma nova OS.\nCampos obrigatórios: descrição, equipamento, cliente e tipo")
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

    @Operation(summary = "Agendar OS", description = "Faz o agendamento de uma OS.\nCampos obrigatórios: id, tecnico e data")
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

    @Operation(summary = "Iniciar Serviço", description = "Inicia o serviço de uma OS.\nCampos obrigatórios: id")
    @PatchMapping("/start/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<StartResponse> start(
            @Valid
            @PathVariable Long id) {
        WorkOrder started = service.start(id);
        StartResponse response = mapper.entityToStartResponse(started);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Terminar Serviço", description = "Finaliza o serviço de uma OS.\nCampos obrigatórios: id e log(resumo) ")
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

    @Operation(summary = "Cancelar Serviço", description = "Cancela uma OS.\nCampos obrigatórios: id e motivo ")
    @PatchMapping("/cancel/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<CancelResponse> cancel(
            @Valid
            @PathVariable Long id,
            @RequestBody CancelWO dto) {
        WorkOrder cancelled = service.cancel(id, dto.reason());
        CancelResponse response = mapper.entityToCancelResponse(cancelled);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
