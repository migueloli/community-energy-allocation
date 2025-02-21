package com.ilo.energyallocation.energy.model;

public enum EnergyType {
    SOLAR(0.12),    // Base price per kWh
    WIND(0.08),     // Wind tends to be cheaper than solar
    HYDRO(0.10),    // Hydroelectric base price
    BIOMASS(0.15),  // Biomass typically more expensive
    GRID(0.20);     // Grid energy usually most expensive

    private final double basePrice;

    EnergyType(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getBasePrice() {
        return basePrice;
    }
}
