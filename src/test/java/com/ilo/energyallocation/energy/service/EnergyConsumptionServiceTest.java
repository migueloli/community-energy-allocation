package com.ilo.energyallocation.energy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.ilo.energyallocation.common.exception.ValidationException;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionHistoryResponseDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.dto.EnergySummaryResponseDTO;
import com.ilo.energyallocation.energy.model.EnergyProduction;
import com.ilo.energyallocation.energy.model.EnergySource;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyProductionRepository;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyConsumptionHistoryService;
import com.ilo.energyallocation.energy.strategy.factory.EnergyConsumptionStrategyFactory;
import com.ilo.energyallocation.energy.strategy.interfaces.EnergyConsumptionStrategy;
import com.ilo.energyallocation.user.model.EnergyPreference;
import com.ilo.energyallocation.user.model.IloUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class EnergyConsumptionServiceTest {
    @Mock
    private EnergyConsumptionStrategyFactory strategyFactory;
    @Mock
    private EnergyProductionRepository productionRepository;
    @Mock
    private IEnergyConsumptionHistoryService consumptionLogService;
    @Mock
    private RemainingEnergyTrackingService remainingEnergyService;

    @InjectMocks
    private EnergyConsumptionService consumptionService;

    @Test
    void consumeEnergy_WithValidRequest_ShouldSucceed() {
        // Given
        IloUser user = createTestUser();
        EnergyConsumptionRequestDTO request = createTestRequest();
        List<EnergyConsumptionStrategy> strategies = createTestStrategies();
        EnergyConsumptionResponseDTO expectedResponse = createTestResponse();

        when(strategyFactory.getStrategiesInPriorityOrder(user)).thenReturn(strategies);
        when(strategies.getFirst().consumeEnergy(anyDouble(), any(), any())).thenReturn(expectedResponse);

        // When
        EnergyConsumptionResponseDTO result = consumptionService.consumeEnergy(request, user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEnergyConsumed()).isEqualTo(expectedResponse.getEnergyConsumed());
        verify(consumptionLogService).logConsumption(eq(user.getId()), anyDouble(), any(), any());
    }

    @Test
    void getEnergySummary_ShouldReturnCorrectSummary() {
        // Given
        String userId = "testUser";
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        List<EnergyConsumptionHistoryResponseDTO> consumptionHistory = createTestConsumptionHistory();
        List<EnergyProduction> productionHistory = createTestProductionHistory();

        when(consumptionLogService.getUserLogsByPeriod(userId, startDate, endDate))
                .thenReturn(consumptionHistory);
        when(productionRepository.findByTimestampBetween(startDate, endDate))
                .thenReturn(productionHistory);

        // When
        EnergySummaryResponseDTO result = consumptionService.getEnergySummary(userId, startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getConsumption().getTotalDemand()).isGreaterThan(0);
    }

    @Test
    void consumeEnergy_WithNoAvailableStrategies_ShouldThrowValidationException() {
        // Given
        IloUser user = createTestUser();
        EnergyConsumptionRequestDTO request = createTestRequest();
        when(strategyFactory.getStrategiesInPriorityOrder(user)).thenReturn(List.of());

        // When/Then
        assertThrows(
                ValidationException.class, () ->
                        consumptionService.consumeEnergy(request, user)
        );
    }

    @Test
    void consumeEnergy_WithZeroAmount_ShouldThrowValidationException() {
        // Given
        IloUser user = createTestUser();
        EnergyConsumptionRequestDTO request = createTestRequest();
        request.setRequiredAmount(0.0);

        // When/Then
        assertThrows(
                ValidationException.class, () ->
                        consumptionService.consumeEnergy(request, user)
        );
    }

    @Test
    void getEnergySummary_WithNoHistory_ShouldReturnEmptySummary() {
        // Given
        String userId = "testUser";
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        when(consumptionLogService.getUserLogsByPeriod(any(), any(), any()))
                .thenReturn(List.of());
        when(productionRepository.findByTimestampBetween(any(), any()))
                .thenReturn(List.of());

        // When
        EnergySummaryResponseDTO result = consumptionService.getEnergySummary(userId, startDate, endDate);

        // Then
        assertThat(result.getConsumption().getTotalDemand()).isZero();
        assertThat(result.getProduction().getProduction()).isEmpty();
    }

    private IloUser createTestUser() {
        return IloUser.builder()
                .id("testId")
                .username("testUser")
                .preference(EnergyPreference.SOLAR)
                .build();
    }

    private EnergyConsumptionRequestDTO createTestRequest() {
        return EnergyConsumptionRequestDTO.builder()
                .requiredAmount(100.0)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private List<EnergyConsumptionStrategy> createTestStrategies() {
        EnergyConsumptionStrategy strategy = mock(EnergyConsumptionStrategy.class);
        return List.of(strategy);
    }

    private EnergyConsumptionResponseDTO createTestResponse() {
        EnergyConsumptionResponseDTO response = new EnergyConsumptionResponseDTO();
        response.setEnergyConsumed(100.0);
        response.setTotalCost(15.0);
        response.addEnergySource(new EnergySource(EnergyType.SOLAR, 100.0));
        return response;
    }

    private List<EnergyConsumptionHistoryResponseDTO> createTestConsumptionHistory() {
        return List.of(
                EnergyConsumptionHistoryResponseDTO.builder()
                        .requestedEnergy(100.0)
                        .sourcesUsed(List.of(new EnergySource(EnergyType.SOLAR, 100.0)))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    private List<EnergyProduction> createTestProductionHistory() {
        return List.of(
                EnergyProduction.builder()
                        .amount(150.0)
                        .type(EnergyType.SOLAR)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
