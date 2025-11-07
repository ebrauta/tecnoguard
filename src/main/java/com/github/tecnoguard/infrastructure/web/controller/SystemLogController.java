package com.github.tecnoguard.infrastructure.web.controller;

import com.github.tecnoguard.core.shared.PageDTO;
import com.github.tecnoguard.domain.shared.models.SystemLog;
import com.github.tecnoguard.infrastructure.persistence.SystemLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@PreAuthorize("hasRole('ADMIN')")
public class SystemLogController {

    private final SystemLogRepository repo;

    public SystemLogController(SystemLogRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<PageDTO<SystemLog>> list(
            @PageableDefault(size = 10, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SystemLog> page = repo.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new PageDTO<>(page));
    }
}
