package com.ilo.energyallocation.user.mapper;

import com.ilo.energyallocation.user.dto.UserRegistrationRequestDTO;
import com.ilo.energyallocation.user.dto.UserResponseDTO;
import com.ilo.energyallocation.user.model.IloUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toDTO(IloUser user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    IloUser toEntity(UserRegistrationRequestDTO registrationDTO);
}