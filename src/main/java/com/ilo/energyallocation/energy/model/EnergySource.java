package com.ilo.energyallocation.energy.model;

import lombok.Data;

@Data
public class EnergySource {
    private EnergyType source;
    private double amount;
}
