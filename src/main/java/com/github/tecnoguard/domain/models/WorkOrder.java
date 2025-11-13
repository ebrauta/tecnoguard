package com.github.tecnoguard.domain.models;

import com.github.tecnoguard.core.exceptions.BusinessException;
import com.github.tecnoguard.domain.enums.UserRole;
import com.github.tecnoguard.domain.enums.WOPriority;
import com.github.tecnoguard.domain.enums.WOStatus;
import com.github.tecnoguard.domain.enums.WOType;
import com.github.tecnoguard.domain.shared.models.AuditableEntity;
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

    private WOStatus status;
    private WOType type;
    private WOPriority priority;

    private String assignedTechnician;
    private LocalDate scheduledDate;

    private LocalDateTime openingDate;
    private LocalDateTime startDate;
    private LocalDateTime closingDate;
    private LocalDateTime cancelDate;

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

