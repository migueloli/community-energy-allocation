package com.ilo.energyallocation.user.service;

import com.ilo.energyallocation.common.exception.TokenException;
import com.ilo.energyallocation.common.security.interfaces.IJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService implements IJwtService {
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final long resetTokenExpiration;
    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret}") String secretKeyString,
                      @Value("${jwt.access-token.expiration}") long accessTokenExpiration, @Value("${jwt" + ".refresh"
                    + "-token.expiration}") long refreshTokenExpiration,
                      @Value("${jwt.reset-token.expiration}") long resetTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.resetTokenExpiration = resetTokenExpiration;
        this.secretKey = generateSecretKey(secretKeyString);
    }

    @Override
    public String generateAccessToken(final UserDetails userDetails) {
        return generateAccessToken(userDetails, new HashMap<>());
    }

    @Override
    public String generateAccessToken(final UserDetails userDetails, final Map<String, Object> extraClaims) {
        return buildToken(userDetails, extraClaims, accessTokenExpiration, "access");
    }

    @Override
    public String generateRefreshToken(final UserDetails userDetails) {
        return generateRefreshToken(userDetails, new HashMap<>());
    }

    @Override
    public String generateRefreshToken(final UserDetails userDetails, final Map<String, Object> extraClaims) {
        return buildToken(userDetails, extraClaims, refreshTokenExpiration, "refresh");
    }

    @Override
    public String generateResetToken(final UserDetails userDetails) {
        return generateRefreshToken(userDetails, new HashMap<>());
    }

    @Override
    public String generateResetToken(final UserDetails userDetails, final Map<String, Object> extraClaims) {
        return buildToken(userDetails, extraClaims, resetTokenExpiration, "reset");
    }

    @Override
    public Claims extractAllClaims(final String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            if (claims == null) {
                throw new JwtException("Invalid claims");
            }
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            throw new JwtException("Token has expired");
        } catch (MalformedJwtException e) {
            throw new JwtException("Malformed token");
        } catch (Exception e) {
            throw new JwtException("Error extracting claims: " + e.getMessage());
        }
    }


    @Override
    public String extractUsername(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public boolean isTokenValid(final String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            throw new TokenException("Invalid token");
        }
    }

    @Override
    public boolean isTokenExpired(final String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            throw new TokenException("Error processing token");
        }
    }

    @Override
    public SecretKey generateSecretKey(final String secretKeyString) {
        final byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    @Override
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    @Override
    public long getResetTokenExpiration() {
        return resetTokenExpiration;
    }

    private String buildToken(final UserDetails userDetails, final Map<String, Object> extraClaims,
                              final long expiration, final String tokenType) {
        return Jwts.builder().claims().subject(userDetails.getUsername()).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + expiration)).add("tokenType", tokenType).add(extraClaims).and().signWith(secretKey).compact();
    }
}