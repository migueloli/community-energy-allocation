package com.ilo.energyallocation.batch.service;

import com.ilo.energyallocation.batch.service.interfaces.IBatchProcessingService;
import com.ilo.energyallocation.energy.repository.CommunityEnergyMetricsRepository;
import com.ilo.energyallocation.energy.repository.EnergyConsumptionRepository;
import com.ilo.energyallocation.energy.repository.EnergyCostRepository;
import com.ilo.energyallocation.energy.repository.EnergyProductionRepository;
import com.ilo.energyallocation.energy.service.RemainingEnergyTrackingService;
import com.ilo.energyallocation.energy.service.interfaces.IDemandCalculationService;
import com.ilo.energyallocation.user.model.IloUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchProcessingService implements IBatchProcessingService {
    private final EnergyProductionRepository productionRepository;
    private final EnergyConsumptionRepository consumptionRepository;
    private final CommunityEnergyMetricsRepository metricsRepository;
    private final EnergyCostRepository costRepository;
    private final CsvDataService csvDataService;
    private final IDemandCalculationService demandCalculationService;
    private final RemainingEnergyTrackingService remainingEnergyService;

    @Transactional
    @Override
    public void processBatchData(
            MultipartFile file,
            IloUser currentUser
    ) {
        log.info("Starting batch processing for file: {}", file.getOriginalFilename());

        // Process CSV data
        csvDataService.processCSV(file, currentUser);

        // Recalculate demand allocation for affected time periods
        demandCalculationService.calculateDemandAllocation();

        log.info("Batch processing completed successfully");
    }

    @Transactional
    @Override
    public void clearBatchData(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Clearing energy data from {} to {}", startDate, endDate);

        // Clear production data
        productionRepository.deleteByTimestampBetween(startDate, endDate);

        // Clear consumption history
        consumptionRepository.deleteByTimestampBetween(startDate, endDate);

        // Clear community metrics
        metricsRepository.deleteByTimestampBetween(startDate, endDate);

        // Clear energy costs for the period
        costRepository.deleteByLastUpdatedBetween(startDate, endDate);

        // Recalculate demand allocation for remaining data
        demandCalculationService.calculateDemandAllocation();

        log.info("Data clearing completed successfully");
    }
}