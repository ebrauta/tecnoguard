package com.github.tecnoguard.infrastructure.persistence;

import com.github.tecnoguard.domain.shared.models.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
}
