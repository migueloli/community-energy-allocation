package com.ilo.energyallocation.batch.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import com.ilo.energyallocation.energy.repository.CommunityEnergyMetricsRepository;
import com.ilo.energyallocation.energy.repository.EnergyConsumptionRepository;
import com.ilo.energyallocation.energy.repository.EnergyCostRepository;
import com.ilo.energyallocation.energy.repository.EnergyProductionRepository;
import com.ilo.energyallocation.energy.service.interfaces.IDemandCalculationService;
import com.ilo.energyallocation.user.model.EnergyPreference;
import com.ilo.energyallocation.user.model.IloUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class BatchProcessingServiceTest {
    @Mock
    private EnergyProductionRepository productionRepository;
    @Mock
    private EnergyConsumptionRepository consumptionRepository;
    @Mock
    private CommunityEnergyMetricsRepository metricsRepository;
    @Mock
    private EnergyCostRepository costRepository;
    @Mock
    private CsvDataService csvDataService;
    @Mock
    private IDemandCalculationService demandCalculationService;

    @InjectMocks
    private BatchProcessingService batchProcessingService;

    @Test
    void processBatchData_ShouldProcessSuccessfully() {
        // Given
        var user = createTestUser();
        MultipartFile mockFile = mock(MultipartFile.class);

        // When
        batchProcessingService.processBatchData(mockFile, user);

        // Then
        verify(csvDataService).processCSV(mockFile, user);
        verify(demandCalculationService).calculateDemandAllocation();
    }

    @Test
    void clearBatchData_ShouldClearDataForGivenPeriod() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        // When
        batchProcessingService.clearBatchData(startDate, endDate);

        // Then
        verify(productionRepository).deleteByTimestampBetween(startDate, endDate);
        verify(consumptionRepository).deleteByTimestampBetween(startDate, endDate);
        verify(metricsRepository).deleteByTimestampBetween(startDate, endDate);
        verify(costRepository).deleteByLastUpdatedBetween(startDate, endDate);
        verify(demandCalculationService).calculateDemandAllocation();
    }

    private IloUser createTestUser() {
        return IloUser.builder()
                .id("testId")
                .username("testUser")
                .preference(EnergyPreference.SOLAR)
                .build();
    }
}
