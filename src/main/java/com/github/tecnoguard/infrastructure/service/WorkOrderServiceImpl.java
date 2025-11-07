package com.github.tecnoguard.infrastructure.service;

import com.github.tecnoguard.core.exceptions.NotFoundException;
import com.github.tecnoguard.domain.models.WorkOrder;
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
    private final ISystemLogService logService;

    public WorkOrderServiceImpl(WorkOrderRepository repo, ISystemLogService logService) {
        this.repo = repo;
        this.logService = logService;
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
        logService.log(
                "WORK_ORDER_CREATED",
                "WORK_ORDER",
                response.getId(),
                String.format("OS criada por %s ", getCurrentUser())
        );
        return response;
    }

    @Override
    @Transactional
    public WorkOrder assign(Long id, String tech, LocalDate date) {
        WorkOrder w = findById(id);
        w.assign(tech, date);
        logService.log(
                "WORK_ORDER_ASSIGNED",
                "WORK_ORDER",
                w.getId(),
                String.format("Técnico %s designado por %s ", tech, getCurrentUser())
        );
        return repo.save(w);
    }

    @Override
    @Transactional
    public WorkOrder start(Long id) {
        WorkOrder w = findById(id);
        w.start();
        logService.log(
                "WORK_ORDER_STARTED",
                "WORK_ORDER",
                w.getId(),
                String.format("OS iniciada por %s ", getCurrentUser())
        );
        return repo.save(w);
    }

    @Override
    @Transactional
    public WorkOrder complete(Long id, String log) {
        WorkOrder w = findById(id);
        w.complete(log);
        logService.log(
                "WORK_ORDER_COMPLETED",
                "WORK_ORDER",
                w.getId(),
                String.format("OS finalizada por %s ", getCurrentUser())
        );
        return repo.save(w);
    }

    @Override
    @Transactional
    public WorkOrder cancel(Long id, String reason) {
        WorkOrder w = findById(id);
        w.cancel(reason);
        logService.log(
                "WORK_ORDER_CANCELLED",
                "WORK_ORDER",
                w.getId(),
                String.format("OS cancelada por %s ", getCurrentUser())
        );
        return repo.save(w);
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
