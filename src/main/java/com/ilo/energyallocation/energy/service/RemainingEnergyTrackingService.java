package com.ilo.energyallocation.energy.service;

import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.model.RemainingEnergy;
import com.ilo.energyallocation.energy.repository.RemainingEnergyRepository;
import com.ilo.energyallocation.energy.service.interfaces.IRemainingEnergyTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RemainingEnergyTrackingService implements IRemainingEnergyTrackingService {
    private final RemainingEnergyRepository remainingEnergyRepository;

    public void initializeTimeSlot(
            LocalDateTime timeSlot, Map<EnergyType, Double> initialProduction, Map<EnergyType, Double> initialDemand) {
        initialProduction.forEach((type, production) -> {
            RemainingEnergy remainingEnergy = RemainingEnergy.builder()
                    .timeSlot(timeSlot)
                    .type(type)
                    .remainingProduction(production)
                    .remainingDemand(initialDemand.getOrDefault(type, 0.0))
                    .build();
            remainingEnergyRepository.save(remainingEnergy);
        });
    }

    public void subtractFromRemainingEnergy(LocalDateTime timeSlot, EnergyType type, double allocatedAmount) {
        var currentTimeSlot = processTimeSlot(timeSlot);
        remainingEnergyRepository.findFirstByTimeSlotAndTypeOrderByTimeSlotDesc(timeSlot, type).ifPresent(energy -> {
            energy.setRemainingProduction(energy.getRemainingProduction() - allocatedAmount);
            energy.setRemainingDemand(energy.getRemainingDemand() - allocatedAmount);
            remainingEnergyRepository.save(energy);
        });
    }

    public void addToRemainingEnergy(LocalDateTime time, EnergyType type, double allocatedAmount) {
        var currentTimeSlot = processTimeSlot(time);
        remainingEnergyRepository.findFirstByTimeSlotAndTypeOrderByTimeSlotDesc(currentTimeSlot, type).ifPresent(
                energy -> {
                    energy.setRemainingProduction(energy.getRemainingProduction() + allocatedAmount);
                    energy.setRemainingDemand(energy.getRemainingDemand() + allocatedAmount);
                    remainingEnergyRepository.save(energy);
                });
    }

    public double getRemainingProduction(LocalDateTime time, EnergyType type) {
        var currentTimeSlot = processTimeSlot(time);
        return remainingEnergyRepository.findFirstByTimeSlotAndTypeOrderByTimeSlotDesc(currentTimeSlot, type)
                .map(RemainingEnergy::getRemainingProduction)
                .orElse(0.0);
    }

    public double getRemainingDemand(LocalDateTime time, EnergyType type) {
        var currentTimeSlot = processTimeSlot(time);
        return remainingEnergyRepository.findFirstByTimeSlotAndTypeOrderByTimeSlotDesc(currentTimeSlot, type)
                .map(RemainingEnergy::getRemainingDemand)
                .orElse(0.0);
    }

    @Override
    public Map<EnergyType, Double> getRemainingEnergy(LocalDateTime time) {
        var currentTimeSlot = processTimeSlot(time);

        Map<EnergyType, Double> remainingEnergy = new EnumMap<>(EnergyType.class);
        Arrays.stream(EnergyType.values()).forEach(type ->
                remainingEnergy.put(type, getRemainingProduction(currentTimeSlot, type))
        );
        return remainingEnergy;
    }

    @Override
    public void consumeEnergy(EnergyType type, double amount, LocalDateTime time) {
        var currentTimeSlot = processTimeSlot(time);

        remainingEnergyRepository.findFirstByTimeSlotAndTypeOrderByTimeSlotDesc(currentTimeSlot, type).ifPresent(
                energy -> {
                    energy.setRemainingProduction(energy.getRemainingProduction() - amount);
                    energy.setRemainingDemand(energy.getRemainingDemand() + amount);
                    remainingEnergyRepository.save(energy);
                });
    }

    @Override
    public LocalDateTime processTimeSlot(LocalDateTime timeSlot) {
        return timeSlot.truncatedTo(ChronoUnit.MINUTES)
                .withMinute((LocalDateTime.now().getMinute() / 15) * 15)
                .withSecond(0)
                .withNano(0);
    }
}


