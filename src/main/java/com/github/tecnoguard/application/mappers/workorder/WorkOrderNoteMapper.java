package com.github.tecnoguard.application.mappers.workorder;

import com.github.tecnoguard.application.dtos.workorder.response.WorkOrderNoteDTO;
import com.github.tecnoguard.domain.models.WorkOrderNote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkOrderNoteMapper {
    WorkOrderNoteDTO toDTO(WorkOrderNote note);
}
