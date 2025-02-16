package com.ilo.energyallocation.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
}