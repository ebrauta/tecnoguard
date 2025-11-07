package com.github.tecnoguard.domain.shared.service;

public interface ISystemLogService {
    void log(String action, String targetType, Long targetId, String details);
}
