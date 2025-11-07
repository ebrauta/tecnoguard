package com.github.tecnoguard.application.dtos.auth.response;

import com.github.tecnoguard.domain.enums.UserRole;

public record UserInfoDTO(String username, String role) {
}
