package com.github.tecnoguard.application.mappers.users;

import com.github.tecnoguard.application.dtos.user.request.CreateUserDTO;
import com.github.tecnoguard.application.dtos.user.request.UpdateUserDTO;
import com.github.tecnoguard.application.dtos.user.response.UserResponseDTO;
import com.github.tecnoguard.domain.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(CreateUserDTO dto) {
        User response = new User();
        response.setUsername(dto.username());
        response.setName(dto.name());
        response.setPassword(dto.password());
        response.setRole(dto.role());
        response.setEmail(dto.email());
        response.setActive(true);
        return response;
    }

    public void updateEntity (User user, UpdateUserDTO dto) {
        if(dto.name() != null) user.setName(dto.name());
        if(dto.email() != null) user.setEmail(dto.email());
        if(dto.role() != null) user.setRole(dto.role());
    }

    public UserResponseDTO toResponse(User user){
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLogin()
        );
    }
}
