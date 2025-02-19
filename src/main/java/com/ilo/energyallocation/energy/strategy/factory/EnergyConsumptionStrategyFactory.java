package com.ilo.energyallocation.energy.strategy.factory;

import com.ilo.energyallocation.energy.strategy.CommunityEnergyStrategy;
import com.ilo.energyallocation.energy.strategy.GridEnergyStrategy;
import com.ilo.energyallocation.energy.strategy.PreferenceConsumptionStrategy;
import com.ilo.energyallocation.energy.strategy.SelfProducedEnergyStrategy;
import com.ilo.energyallocation.energy.strategy.interfaces.DynamicEnergyConsumptionStrategy;
import com.ilo.energyallocation.energy.strategy.interfaces.EnergyConsumptionStrategy;
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
    private final CommunityEnergyStrategy communityStrategy;
    private final List<DynamicEnergyConsumptionStrategy> renewableStrategies;
    private final GridEnergyStrategy gridStrategy;

    public List<EnergyConsumptionStrategy> getStrategiesInPriorityOrder() {
        List<EnergyConsumptionStrategy> strategies = new ArrayList<>();
        strategies.add(preferenceStrategy);
        strategies.add(selfProducedStrategy);
        strategies.add(communityStrategy);

        // Add renewable strategies sorted by current cost
        List<EnergyConsumptionStrategy> sortedRenewableStrategies =
                renewableStrategies.stream().sorted(
                                Comparator.comparingDouble(DynamicEnergyConsumptionStrategy::getCurrentCost))
                        .collect(Collectors.toList());
        strategies.addAll(sortedRenewableStrategies);

        strategies.add(gridStrategy);
        return strategies;
    }
}