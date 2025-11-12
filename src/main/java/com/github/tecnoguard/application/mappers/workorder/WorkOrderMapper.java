package com.github.tecnoguard.application.mappers.workorder;

import com.github.tecnoguard.application.dtos.workorder.request.AssignRequest;
import com.github.tecnoguard.application.dtos.workorder.request.CreateRequest;
import com.github.tecnoguard.application.dtos.workorder.response.*;
import com.github.tecnoguard.domain.models.WorkOrder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface WorkOrderMapper {
    WorkOrder createRequestToEntity(CreateRequest dto);
    CreateResponse entityToCreateResponse(WorkOrder entity);

    AssignResponse entityToAssignResponse(WorkOrder entity);
    StartResponse entityToStartResponse(WorkOrder entity);
    CompleteResponse entityToCompleteResponse(WorkOrder entity);
    CancelResponse entityToCancelResponse(WorkOrder entity);
}
