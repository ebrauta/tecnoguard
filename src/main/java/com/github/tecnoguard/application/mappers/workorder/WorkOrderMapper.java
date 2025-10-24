package com.github.tecnoguard.application.mappers.workorder;

import com.github.tecnoguard.application.dtos.workorder.request.CreateWO;
import com.github.tecnoguard.application.dtos.workorder.response.FullResponseWO;
import com.github.tecnoguard.domain.model.WorkOrder;

public class WorkOrderMapper {
    public FullResponseWO fromEntityToFullDTO(WorkOrder wo) {
        return new FullResponseWO(
                wo.getId(),
                wo.getDescription(),
                wo.getEquipment(),
                wo.getClient(),
                wo.getAssignedTechnician(),
                wo.getScheduledDate(),
                wo.getType(),
                wo.getStatus(),
                wo.getCancelReason(),
                wo.getCompletedAt()
        );
    }

    public WorkOrder fromCreateToEntity(CreateWO dto) {
        return new WorkOrder(dto.description(), dto.equipment(), dto.client(), dto.type());
    }

}
