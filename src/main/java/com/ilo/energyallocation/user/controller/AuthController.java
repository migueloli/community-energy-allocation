package com.ilo.energyallocation.user.controller;

import com.ilo.energyallocation.user.dto.ForgotPasswordRequestDTO;
import com.ilo.energyallocation.user.dto.LoginRequestDTO;
import com.ilo.energyallocation.user.dto.LogoutRequestDTO;
import com.ilo.energyallocation.user.dto.RefreshTokenRequestDTO;
import com.ilo.energyallocation.user.dto.ResetPasswordRequestDTO;
import com.ilo.energyallocation.user.dto.TokenResponseDTO;
import com.ilo.energyallocation.user.service.interfaces.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody final LoginRequestDTO loginRequest) {
        final TokenResponseDTO token = authenticationService.authenticateAndGenerateToken(loginRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refreshToken(@RequestBody final RefreshTokenRequestDTO refreshTokenRequest) {
        final TokenResponseDTO token = authenticationService.refreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody final LogoutRequestDTO logoutRequest) {
        authenticationService.logout(logoutRequest.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody final ForgotPasswordRequestDTO forgotPasswordRequest) {
        authenticationService.initiatePasswordReset(forgotPasswordRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody final ResetPasswordRequestDTO resetPasswordRequest) {
        authenticationService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
