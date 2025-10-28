package com.github.tecnoguard.infrastructure.persistence;

import com.github.tecnoguard.domain.models.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
}
