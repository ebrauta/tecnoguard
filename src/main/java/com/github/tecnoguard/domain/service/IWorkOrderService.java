package com.github.tecnoguard.domain.service;

import com.github.tecnoguard.application.dtos.workorder.request.AssignRequest;
import com.github.tecnoguard.application.dtos.workorder.request.CancelRequest;
import com.github.tecnoguard.application.dtos.workorder.request.CompleteRequest;
import com.github.tecnoguard.domain.models.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IWorkOrderService {
    WorkOrder create(WorkOrder order);
    WorkOrder assign(Long id, AssignRequest dto);
    WorkOrder start(Long id);
    WorkOrder complete(Long id, CompleteRequest dto);
    WorkOrder cancel(Long id, CancelRequest dto);
    Page<WorkOrder> list(Pageable pageable);
    WorkOrder findById(Long id);
}
