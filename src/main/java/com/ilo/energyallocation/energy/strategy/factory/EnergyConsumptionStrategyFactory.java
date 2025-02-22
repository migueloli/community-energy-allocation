package com.ilo.energyallocation.energy.strategy.factory;

import com.ilo.energyallocation.energy.mapper.EnergyPreferenceMapper;
import com.ilo.energyallocation.energy.strategy.GridEnergyStrategy;
import com.ilo.energyallocation.energy.strategy.PreferenceConsumptionStrategy;
import com.ilo.energyallocation.energy.strategy.SelfProducedEnergyStrategy;
import com.ilo.energyallocation.energy.strategy.interfaces.DynamicEnergyConsumptionStrategy;
import com.ilo.energyallocation.energy.strategy.interfaces.EnergyConsumptionStrategy;
import com.ilo.energyallocation.user.model.IloUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EnergyConsumptionStrategyFactory {
    private final PreferenceConsumptionStrategy preferenceStrategy;
    private final SelfProducedEnergyStrategy selfProducedStrategy;
    private final List<DynamicEnergyConsumptionStrategy> renewableStrategies;
    private final GridEnergyStrategy gridStrategy;
    private final EnergyPreferenceMapper energyPreferenceMapper;

    public List<EnergyConsumptionStrategy> getStrategiesInPriorityOrder(IloUser user) {
        List<EnergyConsumptionStrategy> strategies = new ArrayList<>();
        strategies.add(preferenceStrategy);
        strategies.add(selfProducedStrategy);

        // Add renewable strategies sorted by current cost
        final var preferenceEnergyType = energyPreferenceMapper.toEnergyType(user.getPreference());
        final List<EnergyConsumptionStrategy> sortedRenewableStrategies =
                renewableStrategies.stream().filter((strategy) -> strategy.getEnergyType() != preferenceEnergyType)
                        .sorted(
                                Comparator.comparingDouble(DynamicEnergyConsumptionStrategy::getCurrentCost))
                        .collect(Collectors.toList());
        strategies.addAll(sortedRenewableStrategies);

        strategies.add(gridStrategy);
        return strategies;
    }
}