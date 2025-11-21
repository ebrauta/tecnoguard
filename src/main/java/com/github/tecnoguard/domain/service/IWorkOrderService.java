package com.github.tecnoguard.domain.service;

import com.github.tecnoguard.domain.models.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IWorkOrderService {
    WorkOrder create(WorkOrder order);

    WorkOrder assign(Long id, String tech, LocalDate date);

    WorkOrder start(Long id);

    WorkOrder complete(Long id, String log);

    WorkOrder cancel(Long id, String reason);

    Page<WorkOrder> list(Pageable pageable);

    WorkOrder findById(Long id);

    WorkOrder addNote(Long id, String msg);
}
