package com.github.tecnoguard.application.mappers.workorder;

import com.github.tecnoguard.application.dtos.workorder.response.WorkOrderNoteDTO;
import com.github.tecnoguard.domain.models.WorkOrderNote;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public interface WorkOrderNoteMapper {
    WorkOrderNoteDTO toDTO(WorkOrderNote note);
}
