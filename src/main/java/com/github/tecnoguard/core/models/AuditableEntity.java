package com.github.tecnoguard.core.models;

import com.github.tecnoguard.domain.models.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity extends BaseEntity{
    @CreatedBy
    @Column(name = "created_by")
    protected String createdBy;
    @LastModifiedBy
    @Column(name = "updated_by")
    protected String updatedBy;
}
