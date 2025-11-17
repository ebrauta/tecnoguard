package com.github.tecnoguard.infrastructure.persistence;

import com.github.tecnoguard.core.models.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
}
