package com.github.tecnoguard.application.mappers.users;

import com.github.tecnoguard.application.dtos.user.request.CreateUserDTO;
import com.github.tecnoguard.application.dtos.user.request.UpdateUserDTO;
import com.github.tecnoguard.application.dtos.user.response.UserResponseDTO;
import com.github.tecnoguard.domain.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "active", constant = "true")
    User toEntity(CreateUserDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateEntity(@MappingTarget User user, UpdateUserDTO dto);

    UserResponseDTO toResponse(User user);
}
