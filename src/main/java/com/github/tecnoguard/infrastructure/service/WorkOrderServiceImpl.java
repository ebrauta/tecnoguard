package com.github.tecnoguard.infrastructure.service;

import com.github.tecnoguard.core.exceptions.NotFoundException;
import com.github.tecnoguard.domain.models.WorkOrder;
import com.github.tecnoguard.domain.service.IWorkService;
import com.github.tecnoguard.infrastructure.persistence.WorkOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WorkOrderServiceImpl implements IWorkService {

    private final WorkOrderRepository repo;

    public WorkOrderServiceImpl(WorkOrderRepository repo) {
        this.repo = repo;
    }

    @Override
    public WorkOrder create(WorkOrder order) {
        order.create();
        return repo.save(order);
    }

    @Override
    public WorkOrder assign(Long id, String tech, LocalDate date) {
        WorkOrder w = findById(id);
        w.assign(tech, date);
        return repo.save(w);
    }

    @Override
    public WorkOrder start(Long id) {
        WorkOrder w = findById(id);
        w.start();
        return repo.save(w);
    }

    @Override
    public WorkOrder complete(Long id, String log) {
        WorkOrder w = findById(id);
        w.complete(log);
        return repo.save(w);
    }

    @Override
    public WorkOrder cancel(Long id, String reason) {
        WorkOrder w = findById(id);
        w.cancel(reason);
        return repo.save(w);
    }

    @Override
    public List<WorkOrder> list() {
        return repo.findAll();
    }

    @Override
    public WorkOrder findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("OS não encontrada."));
    }
}
