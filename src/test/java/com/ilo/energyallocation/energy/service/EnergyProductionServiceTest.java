package com.ilo.energyallocation.energy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.ilo.energyallocation.common.exception.ResourceNotFoundException;
import com.ilo.energyallocation.common.exception.ValidationException;
import com.ilo.energyallocation.energy.dto.EnergyProductionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionResponseDTO;
import com.ilo.energyallocation.energy.mapper.EnergyProductionMapper;
import com.ilo.energyallocation.energy.model.EnergyProduction;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyProductionRepository;
import com.ilo.energyallocation.energy.service.interfaces.IDemandCalculationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class EnergyProductionServiceTest {
    @Mock
    private EnergyProductionRepository productionRepository;
    @Mock
    private EnergyProductionMapper productionMapper;
    @Mock
    private RemainingEnergyTrackingService remainingEnergyService;
    @Mock
    private IDemandCalculationService demandCalculationService;

    @InjectMocks
    private EnergyProductionService productionService;

    @Test
    void logProduction_ShouldSaveAndReturnProduction() {
        // Given
        String userId = "testUser";
        EnergyProductionRequestDTO request = createTestProductionRequest();
        EnergyProduction production = createTestProduction();
        EnergyProductionResponseDTO expected = createTestProductionResponse();

        when(productionMapper.toEntity(request)).thenReturn(production);
        when(productionRepository.save(any())).thenReturn(production);
        when(productionMapper.toResponse(production)).thenReturn(expected);
        doNothing().when(demandCalculationService).updateEnergyCosts(any());

        // When
        EnergyProductionResponseDTO result = productionService.logProduction(userId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getProduction()).isEqualTo(expected.getProduction());
        verify(remainingEnergyService).initializeTimeSlot(any(), any(), any());
        verify(demandCalculationService).updateEnergyCosts(any());
    }

    @Test
    void logProduction_WithNullTimestamp_ShouldThrowValidationException() {
        // Given
        String userId = "testUser";
        EnergyProductionRequestDTO request = createTestProductionRequest();
        request.setTimestamp(null);

        // When/Then
        assertThrows(
                ValidationException.class, () ->
                        productionService.logProduction(userId, request)
        );
    }

    @Test
    void logProduction_WithFutureTimestamp_ShouldThrowValidationException() {
        // Given
        String userId = "testUser";
        EnergyProductionRequestDTO request = createTestProductionRequest();
        request.setTimestamp(LocalDateTime.now().plusDays(1));

        // When/Then
        assertThrows(
                ValidationException.class, () ->
                        productionService.logProduction(userId, request)
        );
    }

    @Test
    void getProductionHistory_WithNoHistory_ShouldThrowResourceNotFoundException() {
        // Given
        String userId = "testUser";
        when(productionRepository.findByUserIdOrderByTimestampDesc(userId))
                .thenReturn(List.of());

        // When/Then
        assertThrows(
                ResourceNotFoundException.class, () ->
                        productionService.getProductionHistory(userId)
        );
    }

    private EnergyProductionRequestDTO createTestProductionRequest() {
        return EnergyProductionRequestDTO.builder()
                .energyType(EnergyType.SOLAR)
                .production(100.0)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private EnergyProduction createTestProduction() {
        return EnergyProduction.builder()
                .id("testId")
                .userId("testUser")
                .type(EnergyType.SOLAR)
                .amount(100.0)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private EnergyProductionResponseDTO createTestProductionResponse() {
        return EnergyProductionResponseDTO.builder()
                .id("testId")
                .userId("testUser")
                .energyType(EnergyType.SOLAR)
                .production(100.0)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
