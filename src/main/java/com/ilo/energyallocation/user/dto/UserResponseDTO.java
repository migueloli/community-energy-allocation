package com.ilo.energyallocation.user.dto;

import com.ilo.energyallocation.user.model.EnergyPreference;
import com.ilo.energyallocation.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private String id;
    private String username;
    private String email;
    private List<Role> roles;
    private EnergyPreference preference;
}
