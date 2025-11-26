package com.github.tecnoguard.domain.models;

import com.github.tecnoguard.core.exceptions.BusinessException;
import com.github.tecnoguard.domain.enums.WOMaintenanceTrigger;
import com.github.tecnoguard.domain.enums.WOPriority;
import com.github.tecnoguard.domain.enums.WOStatus;
import com.github.tecnoguard.domain.enums.WOType;
import com.github.tecnoguard.core.models.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_workorder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WorkOrder extends AuditableEntity {

    @Column(name = "description")
    private String description;
    @Column(name = "equipment")
    private String equipment;
    @Column(name = "client")
    private String client;

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<WorkOrderNote> notes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WOStatus status;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private WOType type;
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private WOPriority priority;
    @Column(name = "estimated_hours")
    private Double estimatedHours;
    @Column(name = "actual_hours")
    private Double actualHours;
    @Column(name = "estimated_cost")
    private Double estimatedCost;
    @Column(name = "actual_cost")
    private Double actualCost;
    @Column(name = "assigned_technician")
    private String assignedTechnician;
    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;
    @Column(name = "opening_date")
    private LocalDateTime openingDate;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "closing_date")
    private LocalDateTime closingDate;
    @Column(name = "cancel_date")
    private LocalDateTime cancelDate;
    @Column(name = "cancel_reason")
    private String cancelReason;
    @Column(name = "requires_shutdown")
    private Boolean requiresShutdown = false;
    @Column(name = "safety_risk")
    private Boolean safetyRisk = false;
    @Column(name = "maintenance_trigger")
    private WOMaintenanceTrigger trigger = WOMaintenanceTrigger.MANUAL;

    public void create() {
        this.status = WOStatus.OPEN;
        this.openingDate = LocalDateTime.now();
    }

    public void assign() {
        if (this.status != WOStatus.OPEN) throw new BusinessException("Somente OS abertas podem ser agendadas.");
        this.status = WOStatus.SCHEDULED;
    }

    public void start() {
        if (this.status != WOStatus.SCHEDULED) throw new BusinessException("Somente OS agendadas podem ser iniciadas");
        this.status = WOStatus.IN_PROGRESS;
        this.startDate = LocalDateTime.now();
    }

    public void complete() {
        if (this.status != WOStatus.IN_PROGRESS)
            throw new BusinessException("Somente OS em andamento podem ser concluídas.");
        this.status = WOStatus.COMPLETED;
        this.closingDate = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == WOStatus.COMPLETED) throw new BusinessException("OS concluídas não podem ser canceladas.");
        this.cancelDate = LocalDateTime.now();
        this.status = WOStatus.CANCELLED;
    }
}

