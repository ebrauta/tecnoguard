package com.github.tecnoguard.domain.service;

import com.github.tecnoguard.domain.model.WorkOrder;

import java.time.LocalDate;
import java.util.List;

public interface IWorkService {
    WorkOrder create(WorkOrder order);

    WorkOrder assign(Long id, String tech, LocalDate date);

    WorkOrder start(Long id);

    WorkOrder complete(Long id, String log);

    WorkOrder cancel(Long id, String reason);

    List<WorkOrder> list();

    WorkOrder findById(Long id);
}
