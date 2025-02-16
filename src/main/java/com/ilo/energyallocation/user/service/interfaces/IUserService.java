package com.ilo.energyallocation.user.service.interfaces;

import com.ilo.energyallocation.user.dto.ChangePasswordRequestDTO;
import com.ilo.energyallocation.user.dto.UserRegistrationRequestDTO;
import com.ilo.energyallocation.user.dto.UserUpdateRequestDTO;
import com.ilo.energyallocation.user.model.IloUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
    IloUser createUser(UserRegistrationRequestDTO registrationDTO);

    IloUser findByUsername(String username);

    IloUser findByEmail(String email);

    IloUser updateUser(String userId, UserUpdateRequestDTO updateDTO);

    void changePassword(String userId, ChangePasswordRequestDTO changePasswordDTO);

    IloUser getUserById(String userId);
}