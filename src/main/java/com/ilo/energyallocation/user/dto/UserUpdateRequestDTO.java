package com.ilo.energyallocation.user.dto;

import com.ilo.energyallocation.user.model.EnergyPreference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDTO {
    private String email;
    private EnergyPreference preference;
}