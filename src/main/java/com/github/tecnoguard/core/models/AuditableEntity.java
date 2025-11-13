package com.github.tecnoguard.core.models;

import com.github.tecnoguard.domain.models.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@MappedSuperclass
public abstract class AuditableEntity extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    @CreatedBy
    protected User createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    @LastModifiedBy
    protected User updatedBy;
}
