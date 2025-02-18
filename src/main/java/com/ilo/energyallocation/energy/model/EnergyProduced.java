package com.ilo.energyallocation.energy.model;

import lombok.Data;

@Data
public class EnergyProduced {
    private double solar;
    private double wind;
    private double hydro;
    private double biomass;
}
