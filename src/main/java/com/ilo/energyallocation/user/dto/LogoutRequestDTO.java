package com.ilo.energyallocation.user.dto;

import lombok.Data;

@Data
public class LogoutRequestDTO {
    private String refreshToken;
}