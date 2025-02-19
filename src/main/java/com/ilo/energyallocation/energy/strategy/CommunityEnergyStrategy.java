package com.ilo.energyallocation.energy.strategy;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.model.CommunityEnergy;
import com.ilo.energyallocation.energy.model.EnergySource;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.CommunityEnergyRepository;
import com.ilo.energyallocation.energy.strategy.interfaces.DynamicEnergyConsumptionStrategy;
import com.ilo.energyallocation.user.model.IloUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommunityEnergyStrategy implements DynamicEnergyConsumptionStrategy {
    private final CommunityEnergyRepository communityEnergyRepository;

    @Override
    public EnergyConsumptionResponseDTO consumeEnergy(double requiredAmount, IloUser user) {
        EnergyConsumptionResponseDTO result = new EnergyConsumptionResponseDTO();
        result.setStrategyUsed("CommunityStrategy");

        List<CommunityEnergy> availablePool = communityEnergyRepository.findByIsConsumedFalseOrderByTimestampAsc();

        double totalAvailable = availablePool.stream().mapToDouble(CommunityEnergy::getAvailableEnergy).sum();

        double energyToConsume = Math.min(requiredAmount, totalAvailable);

        if (energyToConsume > 0) {
            EnergySource source = new EnergySource();
            source.setSource(EnergyType.COMMUNITY);
            source.setAmount(energyToConsume);

            result.setEnergyConsumed(energyToConsume);
            result.setSourcesUsed(Collections.singletonList(source));
            result.setTotalCost(energyToConsume * getCurrentCost());

            // Mark consumed energy in the pool
            updateCommunityPool(energyToConsume, availablePool);
        }

        return result;
    }

    private void updateCommunityPool(double energyToConsume, List<CommunityEnergy> availablePool) {
        double remainingToConsume = energyToConsume;

        for (CommunityEnergy pool : availablePool) {
            if (remainingToConsume <= 0) break;

            if (pool.getAvailableEnergy() <= remainingToConsume) {
                pool.setConsumed(true);
                remainingToConsume -= pool.getAvailableEnergy();
            } else {
                pool.setAvailableEnergy(pool.getAvailableEnergy() - remainingToConsume);
                remainingToConsume = 0;
            }
            communityEnergyRepository.save(pool);
        }
    }
}
