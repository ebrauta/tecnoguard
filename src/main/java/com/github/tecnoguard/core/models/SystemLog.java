package com.github.tecnoguard.core.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_system_log")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_timestamp")
    private LocalDateTime timestamp;
    @Column(name = "actor_username")
    private String actorUsername;
    private String action;
    @Column(name = "target_type")
    private String targetType;
    @Column(name = "target_id")
    private Long targetId;
    private String details;

}
