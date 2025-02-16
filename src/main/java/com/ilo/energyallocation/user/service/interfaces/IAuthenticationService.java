package com.ilo.energyallocation.user.service.interfaces;

import com.ilo.energyallocation.user.dto.LoginRequestDTO;
import com.ilo.energyallocation.user.dto.TokenResponseDTO;

public interface IAuthenticationService {
    TokenResponseDTO authenticateAndGenerateToken(LoginRequestDTO loginRequest);

    TokenResponseDTO refreshToken(String refreshToken);

    void logout(String refreshToken);

    void initiatePasswordReset(String email);

    void resetPassword(String token, String newPassword);
}