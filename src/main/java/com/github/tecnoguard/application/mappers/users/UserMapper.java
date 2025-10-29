package com.github.tecnoguard.application.mappers.users;

import com.github.tecnoguard.application.dtos.user.request.RegisterUserDTO;
import com.github.tecnoguard.application.dtos.user.response.RegisterReponseDTO;
import com.github.tecnoguard.domain.models.User;

public class UserMapper {
    public User fromRegisterToEntity(RegisterUserDTO dto) {
        User response = new User();
        response.setUsername(dto.username());
        response.setPassword(dto.password());
        response.setRole(dto.role());
        response.setEmail(dto.email());
        return response;
    }

    public RegisterReponseDTO fromUserToResponse(User user) {
        return new RegisterReponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getCreatedAt());
    }
}
