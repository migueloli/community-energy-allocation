package com.ilo.energyallocation.user.service;

import com.ilo.energyallocation.common.exception.UserNotFoundException;
import com.ilo.energyallocation.user.dto.UserRegistrationRequestDTO;
import com.ilo.energyallocation.user.dto.UserResponseDTO;
import com.ilo.energyallocation.user.dto.UserUpdateRequestDTO;
import com.ilo.energyallocation.user.mapper.UserMapper;
import com.ilo.energyallocation.user.model.IloUser;
import com.ilo.energyallocation.user.model.Role;
import com.ilo.energyallocation.user.repository.UserRepository;
import com.ilo.energyallocation.user.service.interfaces.IUserService;
import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public IloUser loadUserByUsername(final String username) {
        return userRepository.findByUsernameOrEmail(username, username).orElseThrow(() -> new UserNotFoundException(
                "User not found with username: " + username));
    }

    @Override
    public UserResponseDTO findByUsernameOrEmail(final String username) {
        return userMapper.toDTO(userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username)));
    }

    @Override
    public UserResponseDTO convertToUserResponseDTO(IloUser user) {
        return userMapper.toDTO(user);
    }

    @Override
    public UserResponseDTO createUser(final UserRegistrationRequestDTO registrationDTO) {
        try {
            final IloUser newUser = userMapper.toEntity(registrationDTO);
            newUser.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
            if (newUser.getRoles() == null || newUser.getRoles().isEmpty()) {
                newUser.setRoles(Collections.singletonList(Role.USER));
            }
            return userMapper.toDTO(userRepository.save(newUser));
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
    public UserResponseDTO updateUser(final String userId, final UserUpdateRequestDTO updateDTO) {
        final IloUser user =
                userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not " + "found " +
                        "with" + " id: " + userId));

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().trim().isEmpty()) {
            user.setEmail(updateDTO.getEmail().trim());
        }

        if (updateDTO.getPreference() != null) {
            user.setPreference(updateDTO.getPreference());
        }

        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO findById(final String userId) {
        return userMapper.toDTO(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User " + "not" + " found with id: " + userId)));
    }

    @Override
    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
}