package com.github.tecnoguard.application.mappers.users;

import com.github.tecnoguard.application.dtos.user.request.CreateUserDTO;
import com.github.tecnoguard.application.dtos.user.request.UpdateUserDTO;
import com.github.tecnoguard.application.dtos.user.response.UserResponseDTO;
import com.github.tecnoguard.domain.models.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    User toEntity(CreateUserDTO dto);

    void updateEntity(@MappingTarget User user, UpdateUserDTO dto);

    UserResponseDTO toResponse(User user);
}
