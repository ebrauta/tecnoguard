package com.github.tecnoguard.core.service;

import com.github.tecnoguard.core.dto.SystemLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISystemLogService {
    Page<SystemLogDTO> list(Pageable pageable);
    void log(String action, String targetType, Long targetId, String details);
}
