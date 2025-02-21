package com.ilo.energyallocation.user.service.interfaces;

import com.ilo.energyallocation.user.dto.UserRegistrationRequestDTO;
import com.ilo.energyallocation.user.dto.UserResponseDTO;
import com.ilo.energyallocation.user.dto.UserUpdateRequestDTO;
import com.ilo.energyallocation.user.model.IloUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface IUserService extends UserDetailsService {
    UserResponseDTO findByUsernameOrEmail(String username);

    UserResponseDTO convertToUserResponseDTO(IloUser user);

    UserResponseDTO createUser(UserRegistrationRequestDTO registrationDTO);

    UserResponseDTO updateUser(String userId, UserUpdateRequestDTO updateDTO);

    UserResponseDTO findById(String userId);

    List<UserResponseDTO> findAllUsers();
}