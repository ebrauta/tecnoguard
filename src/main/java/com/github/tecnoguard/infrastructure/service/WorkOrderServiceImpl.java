package com.github.tecnoguard.infrastructure.service;

import com.github.tecnoguard.application.dtos.workorder.request.AssignRequest;
import com.github.tecnoguard.application.dtos.workorder.request.CancelRequest;
import com.github.tecnoguard.application.dtos.workorder.request.CompleteRequest;
import com.github.tecnoguard.application.mappers.workorder.WorkOrderMapper;
import com.github.tecnoguard.core.exceptions.BusinessException;
import com.github.tecnoguard.core.exceptions.NotFoundException;
import com.github.tecnoguard.core.utils.NoteFormatter;
import com.github.tecnoguard.domain.models.WorkOrder;
import com.github.tecnoguard.domain.service.IWorkOrderNoteService;
import com.github.tecnoguard.domain.service.IWorkOrderService;
import com.github.tecnoguard.core.service.ISystemLogService;
import com.github.tecnoguard.infrastructure.persistence.WorkOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class WorkOrderServiceImpl implements IWorkOrderService {

    private final WorkOrderRepository repo;
    private final IWorkOrderNoteService noteService;
    private final ISystemLogService logService;
    private final NoteFormatter noteFormatter;
    private final WorkOrderMapper mapper;

    public WorkOrderServiceImpl(WorkOrderRepository repo, IWorkOrderNoteService noteService, ISystemLogService logService, NoteFormatter noteFormatter, WorkOrderMapper mapper) {
        this.repo = repo;
        this.noteService = noteService;
        this.logService = logService;
        this.noteFormatter = noteFormatter;
        this.mapper = mapper;
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "SYSTEM";
    }

    @Override
    @Transactional
    public WorkOrder create(WorkOrder order) {
        order.create();
        WorkOrder response = repo.save(order);
        String user = getCurrentUser();
        noteService.addNote(response,noteFormatter.created(user), "SYSTEM");
        logService.log(
                "WORK_ORDER_CREATED",
                "WORK_ORDER",
                response.getId(),
                String.format("OS criada por %s ", user)
        );
        return response;
    }

    @Override
    @Transactional
    public WorkOrder assign(Long id, AssignRequest dto) {
        WorkOrder w = findById(id);
        w.assign();
        if(dto.scheduledDate().isBefore(LocalDateTime.now())) throw new BusinessException("A Data de Agendamento não pode ser anterior a hoje");
        mapper.updateAssign(w, dto);
        WorkOrder response = repo.save(w);
        String user = getCurrentUser();
        noteService.addNote(response, noteFormatter.assigned(response.getAssignedTechnician(), response.getScheduledDate(), user), "SYSTEM");
        logService.log(
                "WORK_ORDER_ASSIGNED",
                "WORK_ORDER",
                w.getId(),
                String.format("Técnico %s designado por %s ", response.getAssignedTechnician(), getCurrentUser())
        );
        return response;
    }

    @Override
    @Transactional
    public WorkOrder start(Long id) {
        WorkOrder w = findById(id);
        w.start();
        WorkOrder response = repo.save(w);
        String user = getCurrentUser();
        noteService.addNote(w, noteFormatter.started(user), "SYSTEM");
        logService.log(
                "WORK_ORDER_STARTED",
                "WORK_ORDER",
                w.getId(),
                String.format("OS iniciada por %s ", getCurrentUser())
        );
        return response;
    }

    @Override
    @Transactional
    public WorkOrder complete(Long id, CompleteRequest dto) {
        WorkOrder w = findById(id);
        w.complete();
        mapper.updateComplete(w, dto);
        WorkOrder response = repo.save(w);
        String user = getCurrentUser();
        noteService.addNote(response, noteFormatter.completed(dto.log(), response.getOpeningDate(), user), "SYSTEM");
        logService.log(
                "WORK_ORDER_COMPLETED",
                "WORK_ORDER",
                w.getId(),
                String.format("OS finalizada por %s ", getCurrentUser())
        );
        return response;
    }

    @Override
    @Transactional
    public WorkOrder cancel(Long id, CancelRequest dto) {
        WorkOrder w = findById(id);
        w.cancel();
        mapper.updateCancel(w, dto);
        WorkOrder response = repo.save(w);
        String user = getCurrentUser();
        noteService.addNote(response, noteFormatter.cancelled(dto.cancelReason(), user), "SYSTEM");
        logService.log(
                "WORK_ORDER_CANCELLED",
                "WORK_ORDER",
                w.getId(),
                String.format("OS cancelada por %s ", getCurrentUser())
        );
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkOrder> list(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkOrder findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("OS não encontrada."));
    }
}
