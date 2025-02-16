package com.ilo.energyallocation.user.dto;

import com.ilo.energyallocation.user.model.EnergyPreference;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateRequestDTO {
    private String email;
    private EnergyPreference preference;
}