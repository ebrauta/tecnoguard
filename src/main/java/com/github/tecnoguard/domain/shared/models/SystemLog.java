package com.github.tecnoguard.domain.shared.models;

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

    private LocalDateTime timestamp;
    private String actorUsername;
    private String action;
    private String targetType;
    private Long targetId;
    private String details;

}
