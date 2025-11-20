package com.github.tecnoguard.infrastructure.service;

import com.github.tecnoguard.core.dto.SystemLogDTO;
import com.github.tecnoguard.core.models.SystemLog;
import com.github.tecnoguard.core.service.ISystemLogService;
import com.github.tecnoguard.infrastructure.persistence.SystemLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class SystemLogServiceImpl implements ISystemLogService {

    private final SystemLogRepository repo;

    public SystemLogServiceImpl(SystemLogRepository repo) {
        this.repo = repo;
    }


    @Override
    public Page<SystemLogDTO> list(Pageable pageable) {
        List<SystemLog> list = repo.findAll();
        List<SystemLogDTO> dtoList = list.stream()
                .map(log ->
                new SystemLogDTO(
                        log.getId(),
                        log.getTimestamp(),
                        log.getActorUsername(),
                        log.getAction(),
                        log.getTargetType(),
                        log.getTargetId(),
                        log.getDetails())
                )
                .toList();
        return new PageImpl<>(dtoList, pageable, dtoList.size());
    }

    @Override
    public void log(String action, String targetType, Long targetId, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String actor = (auth != null && !auth.getName().equals("anonymousUser")) ? auth.getName() : "SYSTEM";
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
