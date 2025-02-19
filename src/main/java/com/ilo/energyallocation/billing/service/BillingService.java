package com.ilo.energyallocation.billing.service;

import com.ilo.energyallocation.billing.dto.BillingSummaryRequestDTO;
import com.ilo.energyallocation.billing.dto.BillingSummaryResponseDTO;
import com.ilo.energyallocation.billing.dto.EnergyUsageDTO;
import com.ilo.energyallocation.billing.service.interfaces.IBillingService;
import com.ilo.energyallocation.billing.strategy.factory.BillingStrategyFactory;
import com.ilo.energyallocation.billing.strategy.interfaces.BillingStrategy;
import com.ilo.energyallocation.energy.model.EnergyProduction;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyCostRepository;
import com.ilo.energyallocation.energy.repository.EnergyProductionRepository;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyConsumptionHistoryService;
import com.ilo.energyallocation.energy.strategy.factory.EnergyConsumptionStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BillingService implements IBillingService {
    private final EnergyConsumptionStrategyFactory strategyFactory;
    private final BillingStrategyFactory billingStrategyFactory;
    private final EnergyCostRepository costRepository;
    private final EnergyProductionRepository productionRepository;
    private final IEnergyConsumptionHistoryService consumptionHistoryService;


    @Override
    public BillingSummaryResponseDTO generateBillingSummary(
            String userId,
            BillingSummaryRequestDTO billingSummaryRequest
    ) {
        BillingSummaryResponseDTO summary = new BillingSummaryResponseDTO();
        summary.setUserId(userId);
        summary.setStartDate(billingSummaryRequest.getStartDate());
        summary.setEndDate(billingSummaryRequest.getEndDate());

        Map<EnergyType, EnergyUsageDTO> usageByType = calculateUsageByType(
                userId,
                billingSummaryRequest.getStartDate(), billingSummaryRequest.getEndDate()
        );
        summary.setUsageByType(usageByType);

        double totalCost = usageByType.values().stream().mapToDouble(EnergyUsageDTO::getCost).sum();
        summary.setTotalCost(totalCost);

        return summary;
    }

    private Map<EnergyType, EnergyUsageDTO> calculateUsageByType(
            String userId, LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        Map<EnergyType, EnergyUsageDTO> usageMap = new EnumMap<>(EnergyType.class);

        List<EnergyProduction> productions = productionRepository.findByUserIdAndTimestampBetween(
                userId, startDate,
                endDate
        );

        for (EnergyProduction production : productions) {
            updateUsageFromProduction(usageMap, production);
        }

        // Calculate costs using billing strategies
        for (Map.Entry<EnergyType, EnergyUsageDTO> entry : usageMap.entrySet()) {
            EnergyType type = entry.getKey();
            EnergyUsageDTO usage = entry.getValue();

            BillingStrategy strategy = billingStrategyFactory.getStrategy(type);
            double cost = strategy.calculateCost(usage.getAmountConsumed(), type);

            usage.setCost(cost);
            usage.setAverageRate(usage.getAmountConsumed() > 0 ? cost / usage.getAmountConsumed() : 0);
        }

        return usageMap;
    }

    private void updateUsageFromProduction(Map<EnergyType, EnergyUsageDTO> usageMap, EnergyProduction production) {
        updateTypeUsage(
                usageMap, EnergyType.SOLAR,
                production.getEnergyProduced().getSolar() - production.getEnergyAvailable().getSolar()
        );
        updateTypeUsage(
                usageMap, EnergyType.WIND,
                production.getEnergyProduced().getWind() - production.getEnergyAvailable().getWind()
        );
        updateTypeUsage(
                usageMap, EnergyType.HYDRO,
                production.getEnergyProduced().getHydro() - production.getEnergyAvailable().getHydro()
        );
        updateTypeUsage(
                usageMap, EnergyType.BIOMASS,
                production.getEnergyProduced().getBiomass() - production.getEnergyAvailable().getBiomass()
        );
    }

    private void updateTypeUsage(Map<EnergyType, EnergyUsageDTO> usageMap, EnergyType type, double amount) {
        usageMap.computeIfAbsent(type, k -> new EnergyUsageDTO()).setAmountConsumed(
                usageMap.get(type).getAmountConsumed() + amount);
    }
}
