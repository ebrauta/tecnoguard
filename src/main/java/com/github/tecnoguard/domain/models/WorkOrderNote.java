package com.github.tecnoguard.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.tecnoguard.core.models.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_workorder_notes")
public class WorkOrderNote extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workorder_id", nullable = false)
    @JsonIgnore
    private WorkOrder workOrder;

    private String message;
    private String author;

}
