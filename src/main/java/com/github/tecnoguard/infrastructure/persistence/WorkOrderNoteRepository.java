package com.github.tecnoguard.infrastructure.persistence;

import com.github.tecnoguard.domain.models.WorkOrderNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderNoteRepository extends JpaRepository<WorkOrderNote, Long> {
    Page<WorkOrderNote> findByWorkOrderIdOrderByCreatedAtDesc(Long workOrderId, Pageable pageable);
}
