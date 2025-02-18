package com.ilo.energyallocation.user.controller;

import com.ilo.energyallocation.common.exception.GlobalExceptionHandler;
import com.ilo.energyallocation.common.ratelimit.RateLimit;
import com.ilo.energyallocation.user.dto.ForgotPasswordRequestDTO;
import com.ilo.energyallocation.user.dto.LoginRequestDTO;
import com.ilo.energyallocation.user.dto.LogoutRequestDTO;
import com.ilo.energyallocation.user.dto.RefreshTokenRequestDTO;
import com.ilo.energyallocation.user.dto.ResetPasswordRequestDTO;
import com.ilo.energyallocation.user.dto.TokenResponseDTO;
import com.ilo.energyallocation.user.service.interfaces.IAuthenticationService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs for user login, token refresh, password reset")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthenticationService authenticationService;

    @PostMapping("/login")
    @RateLimit
    @Operation(
            summary = "Login user",
            description = "Authenticates user credentials and returns JWT access and refresh tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many login attempts",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<TokenResponseDTO> login(
            @Parameter(
                    description = "Login credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequestDTO.class))
            )
            @Valid @RequestBody final LoginRequestDTO loginRequest
    ) {
        final TokenResponseDTO token = authenticationService.authenticateAndGenerateToken(loginRequest);
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Logout user",
            description = "Invalidates the refresh token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully logged out",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid refresh token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/logout")
    @RateLimit
    public ResponseEntity<?> logout(
            @Parameter(
                    description = "Logout request containing refresh token",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LogoutRequestDTO.class))
            )
            @Valid @RequestBody final LogoutRequestDTO logoutRequest
    ) {
        authenticationService.logout(logoutRequest.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Refresh token",
            description = "Generates new access token using a valid refresh token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token successfully refreshed",
                    content = @Content(schema = @Schema(implementation = TokenResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many login attempts",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
            )
    })
    @PostMapping("/refresh")
    @RateLimit
    public ResponseEntity<TokenResponseDTO> refreshToken(
            @Parameter(
                    description = "Refresh token details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RefreshTokenRequestDTO.class))
            )
            @Valid @RequestBody final RefreshTokenRequestDTO refreshTokenRequest
    ) {
        final TokenResponseDTO token = authenticationService.refreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Forgot password",
            description = "Initiates password reset process by sending reset token to user's email"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset initiated",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Email not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/forgot-password")
    @RateLimit
    public ResponseEntity<?> forgotPassword(
            @Parameter(
                    description = "Forgot password request containing email",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ForgotPasswordRequestDTO.class))
            )
            @Valid @RequestBody final ForgotPasswordRequestDTO forgotPasswordRequest
    ) {
        authenticationService.initiatePasswordReset(forgotPasswordRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Reset password",
            description = "Resets user password using the provided reset token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password successfully reset",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/reset-password")
    @RateLimit
    public ResponseEntity<?> resetPassword(
            @Parameter(
                    description = "Reset password request containing token and new password",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ResetPasswordRequestDTO.class))
            )
            @Valid @RequestBody final ResetPasswordRequestDTO resetPasswordRequest
    ) {
        authenticationService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
