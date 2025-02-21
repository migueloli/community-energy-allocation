package com.ilo.energyallocation.energy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.model.RemainingEnergy;
import com.ilo.energyallocation.energy.repository.RemainingEnergyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RemainingEnergyTrackingServiceTest {
    @Mock
    private RemainingEnergyRepository remainingEnergyRepository;

    @InjectMocks
    private RemainingEnergyTrackingService trackingService;

    @Test
    void getRemainingEnergy_ShouldReturnCurrentTimeSlotEnergy() {
        // Given
        LocalDateTime timeSlot = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                .withMinute((LocalDateTime.now().getMinute() / 15) * 15);
        RemainingEnergy solarEnergy = createTestRemainingEnergy(EnergyType.SOLAR, 100.0);
        RemainingEnergy windEnergy = createTestRemainingEnergy(EnergyType.WIND, 150.0);

        when(remainingEnergyRepository.findByTimeSlotAndType(any(), eq(EnergyType.SOLAR)))
                .thenReturn(Optional.of(solarEnergy));
        when(remainingEnergyRepository.findByTimeSlotAndType(any(), eq(EnergyType.WIND)))
                .thenReturn(Optional.of(windEnergy));

        // When
        Map<EnergyType, Double> result = trackingService.getRemainingEnergy();

        // Then
        assertThat(result).containsEntry(EnergyType.SOLAR, 100.0);
        assertThat(result).containsEntry(EnergyType.WIND, 150.0);
    }

    @Test
    void initializeTimeSlot_WithEmptyMaps_ShouldInitializeWithZeros() {
        // Given
        LocalDateTime timeSlot = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                .withMinute((LocalDateTime.now().getMinute() / 15) * 15);
        Map<EnergyType, Double> emptyProduction = new EnumMap<>(EnergyType.class);
        Map<EnergyType, Double> emptyDemand = new EnumMap<>(EnergyType.class);

        // When
        trackingService.initializeTimeSlot(timeSlot, emptyProduction, emptyDemand);

        // Then
        verify(remainingEnergyRepository, never()).save(any());
    }

    @Test
    void consumeEnergy_WhenNoRemainingEnergy_ShouldNotUpdate() {
        // Given
        EnergyType type = EnergyType.SOLAR;
        double amount = 100.0;
        when(remainingEnergyRepository.findByTimeSlotAndType(any(), eq(type)))
                .thenReturn(Optional.empty());

        // When
        trackingService.consumeEnergy(type, amount);

        // Then
        verify(remainingEnergyRepository, never()).save(any());
    }

    @Test
    void getRemainingEnergy_WhenNoDataExists_ShouldReturnZeros() {
        // Given
        when(remainingEnergyRepository.findByTimeSlotAndType(any(), any()))
                .thenReturn(Optional.empty());

        // When
        Map<EnergyType, Double> result = trackingService.getRemainingEnergy();

        // Then
        assertThat(result).containsValue(0.0);
        Arrays.stream(EnergyType.values())
                .forEach(type -> assertThat(result.get(type)).isZero());
    }

    private RemainingEnergy createTestRemainingEnergy(EnergyType type, double amount) {
        return RemainingEnergy.builder()
                .type(type)
                .remainingProduction(amount)
                .timeSlot(LocalDateTime.now())
                .build();
    }
}
