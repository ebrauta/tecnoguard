package com.github.tecnoguard.infrastructure.service;

import com.github.tecnoguard.domain.shared.models.SystemLog;
import com.github.tecnoguard.domain.shared.service.ISystemLogService;
import com.github.tecnoguard.infrastructure.persistence.SystemLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SystemLogServiceImpl implements ISystemLogService {

    private final SystemLogRepository repo;

    public SystemLogServiceImpl(SystemLogRepository repo) {
        this.repo = repo;
    }

    @Override
    public void log(String action, String targetType, Long targetId, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String actor = (auth != null && auth.getName().equals("anonymousUser")) ? auth.getName() : "SYSTEM";
        SystemLog log = new SystemLog();
        log.setTimestamp(LocalDateTime.now());
        log.setActorUsername(actor);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetails(details);

        repo.save(log);
    }
}
