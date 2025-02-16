package com.ilo.energyallocation.user.service;

import com.ilo.energyallocation.common.exception.UserNotFoundException;
import com.ilo.energyallocation.user.dto.ChangePasswordRequestDTO;
import com.ilo.energyallocation.user.dto.UserRegistrationRequestDTO;
import com.ilo.energyallocation.user.dto.UserUpdateRequestDTO;
import com.ilo.energyallocation.user.mapper.UserMapper;
import com.ilo.energyallocation.user.model.IloUser;
import com.ilo.energyallocation.user.model.Role;
import com.ilo.energyallocation.user.repository.UserRepository;
import com.ilo.energyallocation.user.service.interfaces.IUserService;
import com.mongodb.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public IloUser loadUserByUsername(final String username) {
        if (userRepository.existsByUsername(username)) {
            return findByUsername(username);
        }
        return findByEmail(username);
    }

    @Override
    public IloUser createUser(final UserRegistrationRequestDTO registrationDTO) {
        try {
            final IloUser newUser = userMapper.toEntity(registrationDTO);
            newUser.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
            newUser.setRoles(Collections.singletonList(Role.USER));
            return userRepository.save(newUser);
        } catch (DuplicateKeyException e) {
            if (e.getMessage().contains("username")) {
                throw new IllegalArgumentException("Username already exists");
            } else if (e.getMessage().contains("email")) {
                throw new IllegalArgumentException("Email already exists");
            }
            throw e;
        }
    }

    @Override
    public IloUser findByUsername(final String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    public IloUser findByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    public IloUser updateUser(String userId, UserUpdateRequestDTO updateDTO) {
        final IloUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        user.setEmail(updateDTO.getEmail());
        user.setPreference(updateDTO.getPreference());

        return userRepository.save(user);
    }

    @Override
    public void changePassword(String userId, ChangePasswordRequestDTO changePasswordDTO) {
        final IloUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public IloUser getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }
}