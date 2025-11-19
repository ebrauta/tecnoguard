package com.github.tecnoguard.domain.models;

import com.github.tecnoguard.core.exceptions.BusinessException;
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

    private String description;
    private String equipment;

    private String client;

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<WorkOrderNote> notes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private WOStatus status;
    @Enumerated(EnumType.STRING)
    private WOType type;
    @Enumerated(EnumType.STRING)
    private WOPriority priority;
    @Column(name = "assigned_technician")
    private String assignedTechnician;
    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;
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


    public void create() {
        this.status = WOStatus.OPEN;
        this.openingDate = LocalDateTime.now();
    }

    public void assign(String technician, LocalDate date) {
        if (this.status != WOStatus.OPEN) throw new BusinessException("Somente OS abertas podem ser agendadas.");
        if (date.isBefore(LocalDate.now())) throw new BusinessException("A data de agendamento não pode ser anterior a data atual");
        this.assignedTechnician = technician;
        this.scheduledDate = date;
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

    public void cancel(String reason) {
        if (this.status == WOStatus.COMPLETED) throw new BusinessException("OS concluídas não podem ser canceladas.");
        this.cancelReason = reason;
        this.cancelDate = LocalDateTime.now();
        this.status = WOStatus.CANCELLED;
    }
}

