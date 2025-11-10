package com.github.tecnoguard.application.mappers.workorder;

import com.github.tecnoguard.application.dtos.workorder.response.WorkOrderNoteDTO;
import com.github.tecnoguard.domain.models.WorkOrderNote;

public class WorkOrderNoteMapper {
    public WorkOrderNoteDTO toDTO(WorkOrderNote note) {
        return new WorkOrderNoteDTO(note.getId(), note.getMessage(), note.getAuthor(), note.getCreatedAt());
    }
}
