package com.ilo.energyallocation.energy.strategy;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.model.EnergyAvailable;
import com.ilo.energyallocation.energy.model.EnergyProduction;
import com.ilo.energyallocation.energy.model.EnergySource;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyProductionRepository;
import com.ilo.energyallocation.energy.strategy.interfaces.EnergyConsumptionStrategy;
import com.ilo.energyallocation.user.model.IloUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SelfProducedEnergyStrategy implements EnergyConsumptionStrategy {
    private final EnergyProductionRepository productionRepository;

    @Override
    public EnergyConsumptionResponseDTO consumeEnergy(double requiredAmount, IloUser user) {
        List<EnergyProduction> productions = productionRepository.findByUserIdOrderByTimestampDesc(user.getId());

        if (productions.isEmpty()) {
            return null;
        }

        EnergyProduction latestProduction = productions.get(0);
        EnergyAvailable available = latestProduction.getEnergyAvailable();
        double totalAvailable =
                available.getSolar() + available.getWind() + available.getHydro() + available.getBiomass();

        if (totalAvailable <= 0) {
            return null;
        }

        double energyToConsume = Math.min(requiredAmount, totalAvailable);
        EnergyConsumptionResponseDTO result = new EnergyConsumptionResponseDTO();
        result.setStrategyUsed("SelfProducedStrategy");
        result.setEnergyConsumed(energyToConsume);
        result.setTotalCost(0.0); // Self-produced energy is free

        List<EnergySource> sources = calculateEnergySourcesUsed(available, energyToConsume);
        result.setSourcesUsed(sources);

        updateProductionRecord(latestProduction, sources);

        return result;
    }

    private List<EnergySource> calculateEnergySourcesUsed(EnergyAvailable available, double requiredAmount) {
        List<EnergySource> sources = new ArrayList<>();
        double remaining = requiredAmount;

        if (available.getSolar() > 0) {
            addEnergySource(sources, EnergyType.SOLAR, available.getSolar(), remaining);
            remaining -= available.getSolar();
        }
        if (remaining > 0 && available.getWind() > 0) {
            addEnergySource(sources, EnergyType.WIND, available.getWind(), remaining);
            remaining -= available.getWind();
        }
        if (remaining > 0 && available.getHydro() > 0) {
            addEnergySource(sources, EnergyType.HYDRO, available.getHydro(), remaining);
            remaining -= available.getHydro();
        }
        if (remaining > 0 && available.getBiomass() > 0) {
            addEnergySource(sources, EnergyType.BIOMASS, available.getBiomass(), remaining);
        }

        return sources;
    }

    private void addEnergySource(List<EnergySource> sources, EnergyType type, double available, double required) {
        double amount = Math.min(available, required);
        if (amount > 0) {
            EnergySource source = new EnergySource();
            source.setSource(type);
            source.setAmount(amount);
            sources.add(source);
        }
    }

    private void updateProductionRecord(EnergyProduction production, List<EnergySource> sourcesUsed) {
        EnergyAvailable available = production.getEnergyAvailable();

        for (EnergySource source : sourcesUsed) {
            switch (source.getSource()) {
                case SOLAR -> available.setSolar(available.getSolar() - source.getAmount());
                case WIND -> available.setWind(available.getWind() - source.getAmount());
                case HYDRO -> available.setHydro(available.getHydro() - source.getAmount());
                case BIOMASS -> available.setBiomass(available.getBiomass() - source.getAmount());
            }
        }

        double totalConsumed = sourcesUsed.stream().mapToDouble(EnergySource::getAmount).sum();

        production.setConsumedEnergy(production.getConsumedEnergy() + totalConsumed);
        productionRepository.save(production);
    }
}
