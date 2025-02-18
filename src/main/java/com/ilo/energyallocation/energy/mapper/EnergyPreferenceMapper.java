package com.ilo.energyallocation.energy.mapper;

import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.user.model.EnergyPreference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnergyPreferenceMapper {
    @Mapping(target = ".", source = "preference")
    default EnergyType toEnergyType(EnergyPreference preference) {
        if (preference == EnergyPreference.NO_PREFERENCE) {
            return null;
        }
        return EnergyType.valueOf(preference.name());
    }
}
