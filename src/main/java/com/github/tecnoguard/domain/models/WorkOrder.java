package com.github.tecnoguard.domain.models;

import com.github.tecnoguard.core.exceptions.BusinessException;
import com.github.tecnoguard.domain.enums.WOStatus;
import com.github.tecnoguard.domain.enums.WOType;
import com.github.tecnoguard.domain.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_workorder")
@NoArgsConstructor
@Getter
public class WorkOrder extends BaseEntity {

    private String description;
    private String equipment;

    private String client;
    private final List<String> workOrderLog = new ArrayList<>();
    private WOStatus status;
    private WOType type;

    private String assignedTechnician;
    private LocalDate scheduledDate;
    private LocalDateTime completedAt;

    private String cancelReason;

    public WorkOrder(
            String description,
            String equipment,
            String client,
            WOType type) {
        this.description = description;
        this.equipment = equipment;
        this.client = client;
        this.type = type;

    }

    public void create() {
        this.status = WOStatus.OPEN;
        this.workOrderLog.add("OS criada em: " + this.createdAt.toLocalDate());
    }


    public void assign(String technician, LocalDate date) {
        if (this.status != WOStatus.OPEN) throw new BusinessException("Somente OS abertas podem ser agendadas.");
        this.assignedTechnician = technician;
        this.scheduledDate = date;
        this.status = WOStatus.SCHEDULED;
        this.workOrderLog.add("OS agendada em: " + date);
    }

    public void start() {
        if (this.status != WOStatus.SCHEDULED) throw new BusinessException("Somente OS agendadas podem ser iniciadas");
        this.status = WOStatus.IN_PROGRESS;
        this.workOrderLog.add("OS iniciada em: " + LocalDate.now());
    }

    public void complete(String log) {
        if (this.status != WOStatus.IN_PROGRESS)
            throw new BusinessException("Somente OS em andamento podem ser concluídas.");
        this.workOrderLog.add(log);
        this.completedAt = LocalDateTime.now();
        this.workOrderLog.add("Os concluída em: " + this.completedAt.toLocalDate());
        this.status = WOStatus.COMPLETED;
    }

    public void cancel(String reason) {
        if (this.status == WOStatus.COMPLETED) throw new BusinessException("OS concluídas não podem ser canceladas.");
        this.cancelReason = reason;
        this.workOrderLog.add("Os cancelada em:" + LocalDate.now());
        this.status = WOStatus.CANCELLED;
    }
}

