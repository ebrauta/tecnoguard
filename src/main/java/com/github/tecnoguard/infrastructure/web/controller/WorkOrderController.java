package com.github.tecnoguard.infrastructure.web.controller;

import com.github.tecnoguard.application.dtos.workorder.request.AssignWO;
import com.github.tecnoguard.application.dtos.workorder.request.CancelWO;
import com.github.tecnoguard.application.dtos.workorder.request.CompleteWO;
import com.github.tecnoguard.application.dtos.workorder.request.CreateWO;
import com.github.tecnoguard.application.dtos.workorder.response.FullResponseWO;
import com.github.tecnoguard.application.mappers.workorder.WorkOrderMapper;
import com.github.tecnoguard.core.shared.PageDTO;
import com.github.tecnoguard.domain.models.WorkOrder;
import com.github.tecnoguard.domain.service.IWorkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Work Orders - Ordens de Serviço", description = "Gestão do ciclo de vida das Ordens de Serviço")
@RestController
@RequestMapping("/api/workorders")
public class WorkOrderController {

    private final IWorkService service;
    private final WorkOrderMapper mapper = new WorkOrderMapper();

    public WorkOrderController(IWorkService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todas", description = "Lista todas as OS.")
    @GetMapping
    public ResponseEntity<PageDTO<FullResponseWO>> list(
            @PageableDefault(size = 10, sort = "equipment", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<FullResponseWO> page = service.list(pageable).map(mapper::fromEntityToFullDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new PageDTO<>(page));
    }

    @Operation(summary = "Listar Log da OS", description = "Mostrar documentação da OS.")
    @GetMapping("/log/{id}")
    public ResponseEntity<PageDTO<String>> log(
            @PageableDefault(size = 10)
            Pageable pageable,
            @PathVariable Long id
    ) {
        WorkOrder wo = service.findById(id);
        Page<String> page = wo.getLogs(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new PageDTO<>(page));
    }

    @Operation(summary = "Detalhar OS", description = "Mostra detalhes da OS.\nCampo obrigatório: id")
    @GetMapping("/{id}")
    public ResponseEntity<FullResponseWO> get(@PathVariable Long id) {
        WorkOrder wo = service.findById(id);
        FullResponseWO response = mapper.fromEntityToFullDTO(wo);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Criar nova OS", description = "Cria uma nova OS.\nCampos obrigatórios: descrição, equipamento, cliente e tipo")
    @PostMapping
    public ResponseEntity<FullResponseWO> create(@RequestBody CreateWO dto) {
        WorkOrder wo = mapper.fromCreateToEntity(dto);
        WorkOrder created = service.create(wo);
        FullResponseWO response = mapper.fromEntityToFullDTO(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Agendar OS", description = "Faz o agendamento de uma OS.\nCampos obrigatórios: id, tecnico e data")
    @PatchMapping("/assign/{id}")
    public ResponseEntity<FullResponseWO> assign(@PathVariable Long id,
                                                 @RequestBody AssignWO dto) {
        WorkOrder assigned = service.assign(id, dto.tech(), dto.date());
        FullResponseWO response = mapper.fromEntityToFullDTO(assigned);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Iniciar Serviço", description = "Inicia o serviço de uma OS.\nCampos obrigatórios: id")
    @PatchMapping("/start/{id}")
    public ResponseEntity<FullResponseWO> start(@PathVariable Long id) {
        WorkOrder started = service.start(id);
        FullResponseWO response = mapper.fromEntityToFullDTO(started);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Terminar Serviço", description = "Finaliza o serviço de uma OS.\nCampos obrigatórios: id e log(resumo) ")
    @PatchMapping("/complete/{id}")
    public ResponseEntity<FullResponseWO> complete(@PathVariable Long id, @RequestBody CompleteWO dto) {
        WorkOrder completed = service.complete(id, dto.log());
        FullResponseWO response = mapper.fromEntityToFullDTO(completed);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Cancelar Serviço", description = "Cancela uma OS.\nCampos obrigatórios: id e motivo ")
    @PatchMapping("/cancel/{id}")
    public ResponseEntity<FullResponseWO> cancel(@PathVariable Long id, @RequestBody CancelWO dto) {
        WorkOrder cancelled = service.cancel(id, dto.reason());
        FullResponseWO response = mapper.fromEntityToFullDTO(cancelled);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
