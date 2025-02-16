package com.ilo.energyallocation.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenRequestDTO {
    private String refreshToken;
}