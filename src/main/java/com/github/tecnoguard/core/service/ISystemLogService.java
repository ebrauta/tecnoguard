package com.github.tecnoguard.core.service;

public interface ISystemLogService {
    void log(String action, String targetType, Long targetId, String details);
}
