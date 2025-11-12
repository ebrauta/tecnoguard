package com.github.tecnoguard.infrastructure.service;

import com.github.tecnoguard.core.exceptions.NotFoundException;
import com.github.tecnoguard.core.utils.NoteFormatter;
import com.github.tecnoguard.domain.models.WorkOrder;
import com.github.tecnoguard.domain.service.IWorkOrderNoteService;
import com.github.tecnoguard.domain.service.IWorkService;
import com.github.tecnoguard.domain.shared.service.ISystemLogService;
import com.github.tecnoguard.infrastructure.persistence.WorkOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class WorkOrderServiceImpl implements IWorkService {

    private final WorkOrderRepository repo;
    private final IWorkOrderNoteService noteService;
    private final ISystemLogService logService;
    private final NoteFormatter noteFormatter;

    public WorkOrderServiceImpl(WorkOrderRepository repo, IWorkOrderNoteService noteService, ISystemLogService logService, NoteFormatter noteFormatter) {
        this.repo = repo;
        this.noteService = noteService;
        this.logService = logService;
        this.noteFormatter = noteFormatter;
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
    public WorkOrder assign(Long id, String tech, LocalDate date) {
        WorkOrder w = findById(id);
        w.assign(tech, date);
        WorkOrder response = repo.save(w);
        String user = getCurrentUser();
        noteService.addNote(response, noteFormatter.assigned(tech, date, user), "SYSTEM");
        logService.log(
                "WORK_ORDER_ASSIGNED",
                "WORK_ORDER",
                w.getId(),
                String.format("Técnico %s designado por %s ", tech, getCurrentUser())
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
    public WorkOrder complete(Long id, String log) {
        WorkOrder w = findById(id);
        w.complete(log);
        WorkOrder response = repo.save(w);
        String user = getCurrentUser();
        noteService.addNote(response, noteFormatter.completed(log, response.getOpeningDate(), user), "SYSTEM");
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
    public WorkOrder cancel(Long id, String reason) {
        WorkOrder w = findById(id);
        w.cancel(reason);
        WorkOrder response = repo.save(w);
        String user = getCurrentUser();
        noteService.addNote(response, noteFormatter.cancelled(reason, user), "SYSTEM");
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

    @Override
    @Transactional
    public WorkOrder addNote(Long id, String message) {
        WorkOrder w = findById(id);
        WorkOrder response = repo.save(w);
        String user = getCurrentUser();
        noteService.addNote(w, noteFormatter.format(message, user), user);
        logService.log(
                "WORK_ORDER_NOTE_ADDED",
                "WORK_ORDER_NOTE",
                w.getId(),
                String.format("Nota adicionada por %s (OS #%s)", getCurrentUser(), id)
        );
        return response;
    }
}
