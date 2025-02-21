package com.ilo.energyallocation.energy.service.interfaces;

import com.ilo.energyallocation.energy.model.EnergyType;

import java.time.LocalDateTime;

public interface IDemandCalculationService {
    void calculateDemandAllocation();

    void updateEnergyCosts(LocalDateTime timeStep);

    double calculateNewCost(EnergyType type, LocalDateTime timeStep);
}
