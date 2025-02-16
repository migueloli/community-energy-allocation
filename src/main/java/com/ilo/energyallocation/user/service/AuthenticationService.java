package com.ilo.energyallocation.user.service;

import com.ilo.energyallocation.common.exception.AuthenticationException;
import com.ilo.energyallocation.common.exception.TokenException;
import com.ilo.energyallocation.common.exception.UserNotFoundException;
import com.ilo.energyallocation.common.security.interfaces.IJwtService;
import com.ilo.energyallocation.user.dto.ChangePasswordRequestDTO;
import com.ilo.energyallocation.user.dto.LoginRequestDTO;
import com.ilo.energyallocation.user.dto.TokenResponseDTO;
import com.ilo.energyallocation.user.model.IloUser;
import com.ilo.energyallocation.user.service.interfaces.IAuthenticationService;
import com.ilo.energyallocation.user.service.interfaces.IUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements IAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;
    private final IUserService userService;

    public AuthenticationService(AuthenticationManager authenticationManager, IJwtService jwtService, IUserService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userDetailsService;
    }

    @Override
    public TokenResponseDTO authenticateAndGenerateToken(final LoginRequestDTO loginRequest) {
        // For a real-world application, you would typically store and manage refresh tokens securely.
        // e.g. store them in a database and validate them when refreshing tokens.
        // or use a OAuth2 library to handle token management.
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return generateTokenResponse(userDetails);
        } catch (Exception e) {
            throw new AuthenticationException("Invalid username or password");
        }
    }

    @Override
    public TokenResponseDTO refreshToken(final String refreshToken) {
        // For a real-world application, you would typically store and manage refresh tokens securely.
        // e.g. store them in a database and validate them when refreshing tokens.
        // or use a OAuth2 library to handle token management.
        final String username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            UserDetails userDetails = this.userService.loadUserByUsername(username);
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                return generateTokenResponse(userDetails);
            }
        }
        throw new TokenException("Invalid refresh token");
    }

    @Override
    public void logout(String refreshToken) {
        // For a real-world application, you would typically store and manage refresh tokens securely.
        // e.g. store them in a database and validate them when refreshing tokens.
        // or use a OAuth2 library to handle token management.
    }

    @Override
    public void initiatePasswordReset(String email) {
        UserDetails userDetails = userService.loadUserByUsername(email);
        if (userDetails == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        // Generate password reset token
        String resetToken = jwtService.generatePasswordResetToken(userDetails);

        /*
          Here it would call a separate service or send an event to a queue
          to send the email with the reset token
          e.g. emailService.sendPasswordResetEmail(email, resetToken);
         */
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        String username = jwtService.extractUsername(token);
        if (username == null) {
            throw new TokenException("Invalid reset token");
        }

        IloUser user = (IloUser) userService.loadUserByUsername(username);
        if (!jwtService.isTokenValid(token, user)) {
            throw new TokenException("Invalid or expired reset token");
        }

        userService.changePassword(
                user.getId(),
                ChangePasswordRequestDTO.builder().newPassword(newPassword).build()
        );
    }

    private TokenResponseDTO generateTokenResponse(UserDetails userDetails) {
        final String accessToken = jwtService.generateAccessToken(userDetails);
        final String refreshToken = jwtService.generateRefreshToken(userDetails);
        final long expiresIn = jwtService.getAccessTokenExpiration();

        return TokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .build();
    }
}