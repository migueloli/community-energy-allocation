package com.ilo.energyallocation.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgotPasswordRequestDTO {
    private String email;
}