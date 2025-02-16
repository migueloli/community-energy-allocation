package com.ilo.energyallocation.common.security.interfaces;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface IJwtService {
    String generateAccessToken(UserDetails userDetails);

    String generateAccessToken(UserDetails userDetails, Map<String, Object> extraClaims);

    String generateRefreshToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails, Map<String, Object> extraClaims);

    String generateResetToken(UserDetails userDetails);

    String generateResetToken(UserDetails userDetails, Map<String, Object> extraClaims);

    Claims extractAllClaims(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String extractUsername(String token);

    Date extractExpiration(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    boolean isTokenExpired(String token);

    Key generateSecretKey(String secretKeyString);

    long getAccessTokenExpiration();

    long getRefreshTokenExpiration();

    long getResetTokenExpiration();
}