package com.github.tecnoguard.application.mappers.workorder;

import com.github.tecnoguard.application.dtos.workorder.request.AssignRequest;
import com.github.tecnoguard.application.dtos.workorder.request.CancelRequest;
import com.github.tecnoguard.application.dtos.workorder.request.CompleteRequest;
import com.github.tecnoguard.application.dtos.workorder.request.CreateRequest;
import com.github.tecnoguard.application.dtos.workorder.response.*;
import com.github.tecnoguard.domain.models.WorkOrder;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WorkOrderMapper {
    WorkOrder createRequestToEntity(CreateRequest dto);

    CreateResponse entityToCreateResponse(WorkOrder entity);

    void updateAssign(@MappingTarget WorkOrder wo, AssignRequest dto);

    void updateComplete(@MappingTarget WorkOrder wo, CompleteRequest dto);

    void updateCancel(@MappingTarget WorkOrder wo, CancelRequest dto);

    AssignResponse entityToAssignResponse(WorkOrder entity);

    StartResponse entityToStartResponse(WorkOrder entity);

    CompleteResponse entityToCompleteResponse(WorkOrder entity);

    CancelResponse entityToCancelResponse(WorkOrder entity);
}
