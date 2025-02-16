package com.ilo.energyallocation.user.dto;

import com.ilo.energyallocation.user.model.EnergyPreference;
import com.ilo.energyallocation.user.model.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponseDTO {
    private String id;
    private String username;
    private String email;
    private List<Role> roles;
    private EnergyPreference preference;
}
