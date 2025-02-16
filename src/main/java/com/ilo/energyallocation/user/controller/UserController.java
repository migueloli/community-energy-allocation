package com.ilo.energyallocation.user.controller;

import com.ilo.energyallocation.user.dto.ChangePasswordRequestDTO;
import com.ilo.energyallocation.user.dto.UserRegistrationRequestDTO;
import com.ilo.energyallocation.user.dto.UserResponseDTO;
import com.ilo.energyallocation.user.dto.UserUpdateRequestDTO;
import com.ilo.energyallocation.user.mapper.UserMapper;
import com.ilo.energyallocation.user.model.IloUser;
import com.ilo.energyallocation.user.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody final UserRegistrationRequestDTO registrationDTO) {
        final IloUser createdUser = userService.createUser(registrationDTO);
        return ResponseEntity.ok(userMapper.toDTO(createdUser));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal final IloUser user) {
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateUser(@AuthenticationPrincipal final IloUser user, @RequestBody final UserUpdateRequestDTO updateDTO) {
        IloUser updatedUser = userService.updateUser(user.getId(), updateDTO);
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal final IloUser user, @RequestBody final ChangePasswordRequestDTO changePasswordDTO) {
        userService.changePassword(user.getId(), changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable final String userId) {
        IloUser user = userService.getUserById(userId);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }
}