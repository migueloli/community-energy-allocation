package com.ilo.energyallocation.user.controller;

import com.ilo.energyallocation.common.exception.GlobalExceptionHandler;
import com.ilo.energyallocation.common.ratelimit.RateLimit;
import com.ilo.energyallocation.user.dto.ChangePasswordRequestDTO;
import com.ilo.energyallocation.user.dto.UserRegistrationRequestDTO;
import com.ilo.energyallocation.user.dto.UserResponseDTO;
import com.ilo.energyallocation.user.dto.UserUpdateRequestDTO;
import com.ilo.energyallocation.user.model.IloUser;
import com.ilo.energyallocation.user.service.interfaces.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/v1/api/users")
@Tag(name = "Users", description = "Endpoints for managing user profiles, including registration, profile updates, and admin operations")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already exists",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            )
    })
    @PostMapping("/register")
    @RateLimit
    public ResponseEntity<UserResponseDTO> registerUser(
            @Parameter(
                    description = "User registration details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRegistrationRequestDTO.class))
            )
            @Valid @RequestBody final UserRegistrationRequestDTO registrationDTO
    ) {
        return ResponseEntity.ok(userService.createUser(registrationDTO));
    }

    @Operation(
            summary = "Get current user",
            description = "Retrieves the authenticated user's profile data"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            )
    })
    @GetMapping("/me")
    @RateLimit
    public ResponseEntity<UserResponseDTO> getCurrentUser(
            @Parameter(
                    description = "Authenticated user context",
                    hidden = true
            )
            @AuthenticationPrincipal final IloUser user
    ) {
        return ResponseEntity.ok(userService.convertToUserResponseDTO(user));
    }

    @Operation(
            summary = "Update current user",
            description = "Updates the authenticated user's profile information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            )
    })
    @PutMapping("/me")
    @RateLimit
    public ResponseEntity<UserResponseDTO> updateUser(
            @AuthenticationPrincipal final IloUser user,
            @Parameter(
                    description = "User profile update details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserUpdateRequestDTO.class))
            )
            @Valid @RequestBody final UserUpdateRequestDTO updateDTO
    ) {
        return ResponseEntity.ok(userService.updateUser(user.getId(), updateDTO));
    }

    @Operation(
            summary = "Change password of current user",
            description = "Changes the password of the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password successfully updated",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            )
    })
    @PostMapping("/change-password")
    @RateLimit
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal final IloUser user,
            @Parameter(
                    description = "Password change details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChangePasswordRequestDTO.class))
            )
            @Valid @RequestBody final ChangePasswordRequestDTO changePasswordDTO
    ) {
        userService.changePassword(user.getId(), changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "[Admin] Get user by ID",
            description = "Retrieves user profile data by ID. Only accessible by administrators"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            )
    })
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @RateLimit
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(
                    description = "ID of the user to retrieve",
                    required = true,
                    example = "507f1f77bcf86cd799439011"
            )
            @PathVariable final String userId
    ) {
        return ResponseEntity.ok(userService.findById(userId));
    }
}