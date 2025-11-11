package com.github.tecnoguard.domain.service;

import com.github.tecnoguard.domain.models.WorkOrder;
import com.github.tecnoguard.domain.models.WorkOrderNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IWorkOrderNoteService {
    Page<WorkOrderNote> listNotes(Long workorder_id, Pageable pageable);
    WorkOrderNote addNote(WorkOrder wo, String message, String author);
}
