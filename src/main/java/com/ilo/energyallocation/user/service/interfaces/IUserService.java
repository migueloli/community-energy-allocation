package com.ilo.energyallocation.user.service.interfaces;

import com.ilo.energyallocation.user.dto.ChangePasswordRequestDTO;
import com.ilo.energyallocation.user.dto.UserRegistrationRequestDTO;
import com.ilo.energyallocation.user.dto.UserResponseDTO;
import com.ilo.energyallocation.user.dto.UserUpdateRequestDTO;
import com.ilo.energyallocation.user.model.IloUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
    UserResponseDTO findByUsernameOrEmail(String username);

    UserResponseDTO convertToUserResponseDTO(IloUser user);

    UserResponseDTO createUser(UserRegistrationRequestDTO registrationDTO);

    UserResponseDTO updateUser(String userId, UserUpdateRequestDTO updateDTO);

    void changePassword(String userId, ChangePasswordRequestDTO changePasswordDTO);

    UserResponseDTO findById(String userId);
}