package com.ilo.energyallocation.billing.strategy.factory;

import com.ilo.energyallocation.billing.strategy.CommunityBillingStrategy;
import com.ilo.energyallocation.billing.strategy.GridBillingStrategy;
import com.ilo.energyallocation.billing.strategy.SelfProducedBillingStrategy;
import com.ilo.energyallocation.billing.strategy.interfaces.BillingStrategy;
import com.ilo.energyallocation.billing.strategy.interfaces.RenewableBillingStrategy;
import com.ilo.energyallocation.energy.model.EnergyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillingStrategyFactory {
    private final SelfProducedBillingStrategy selfProducedStrategy;
    private final CommunityBillingStrategy communityStrategy;
    private final RenewableBillingStrategy renewableStrategy;
    private final GridBillingStrategy gridStrategy;

    public BillingStrategy getStrategy(EnergyType sourceType) {
        return switch (sourceType) {
            case SOLAR, WIND, HYDRO, BIOMASS -> renewableStrategy;
            case COMMUNITY -> communityStrategy;
            case GRID -> gridStrategy;
            default -> selfProducedStrategy;
        };
    }
}
