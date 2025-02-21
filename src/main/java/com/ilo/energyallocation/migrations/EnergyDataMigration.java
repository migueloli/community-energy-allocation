package com.ilo.energyallocation.migrations;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionRequestDTO;
import com.ilo.energyallocation.energy.model.EnergyConsumptionHistory;
import com.ilo.energyallocation.energy.model.EnergyCost;
import com.ilo.energyallocation.energy.model.EnergyProduction;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyCostRepository;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyConsumptionService;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyProductionService;
import com.ilo.energyallocation.user.model.IloUser;
import com.ilo.energyallocation.user.repository.UserRepository;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@ChangeUnit(id = "energyDataMigration001", order = "002", author = "miguel", systemVersion = "1")
public class EnergyDataMigration {
    private final UserRepository userRepository;
    private final IEnergyProductionService productionService;
    private final IEnergyConsumptionService consumptionService;
    private final EnergyCostRepository costRepository;
    private final MongoTemplate template;

    @Execution
    public void addInitialEnergyData() {
        createAndSaveEnergyCosts();
        createInitialProductionAndConsumption();
    }

    private void createInitialProductionAndConsumption() {
        List<IloUser> users = userRepository.findAll();
        LocalDateTime baseTime = LocalDateTime.now().minusWeeks(8);

        users.forEach(user -> {
            // Create production data for 8 weeks
            for (int week = 0; week < 8; week++) {
                for (int day = 0; day < 7; day++) {
                    for (int hour = 0; hour < 24; hour += 3) {
                        LocalDateTime timestamp = baseTime.plusWeeks(week).plusDays(day).plusHours(hour);
                        createDiversifiedProduction(user.getId(), timestamp);
                    }
                }
            }

            // Create consumption data for 8 weeks
            Random random = new Random();
            for (int week = 0; week < 8; week++) {
                for (int day = 0; day < 7; day++) {
                    for (int hour = 0; hour < 24; hour++) {
                        LocalDateTime timestamp = baseTime.plusWeeks(week).plusDays(day).plusHours(hour);
                        double consumption = generateConsumptionAmount(hour, day, random);
                        createAndSaveConsumption(user, consumption, timestamp);
                    }
                }
            }
        });
    }

    private double generateConsumptionAmount(int hour, int day, Random random) {
        // Peak hours: 7-9 AM and 6-8 PM on weekdays
        boolean isPeakHour = (hour >= 7 && hour <= 9) || (hour >= 18 && hour <= 20);
        boolean isWeekday = day < 5;

        if (isPeakHour && isWeekday) {
            return 30 + random.nextDouble() * 40; // Higher consumption during peak hours
        }
        return 10 + random.nextDouble() * 20; // Base consumption
    }

    private void createDiversifiedProduction(String userId, LocalDateTime timestamp) {
        Arrays.stream(EnergyType.values())
                .filter(type -> type != EnergyType.GRID)
                .forEach(type -> {
                    double amount = generateRealisticAmount(type, timestamp.getHour());
                    createAndSaveProduction(userId, type, amount, timestamp);
                });
    }

    private double generateRealisticAmount(EnergyType type, int hour) {
        Random random = new Random();
        double baseAmount = switch (type) {
            case SOLAR -> (hour >= 6 && hour <= 18) ? 50 + random.nextDouble() * 50 : 5;
            case WIND -> 30 + random.nextDouble() * 40;
            case HYDRO -> 40 + random.nextDouble() * 30;
            case BIOMASS -> 20 + random.nextDouble() * 30;
            default -> 0;
        };
        return Math.max(0, baseAmount);
    }

    private void createAndSaveProduction(String userId, EnergyType type, double amount, LocalDateTime timestamp) {
        final EnergyProductionRequestDTO request = EnergyProductionRequestDTO.builder()
                .energyType(type)
                .production(amount)
                .timestamp(timestamp)
                .build();
        productionService.logProduction(userId, request);
    }

    private void createAndSaveConsumption(IloUser user, double amount, LocalDateTime timestamp) {
        final EnergyConsumptionRequestDTO request = EnergyConsumptionRequestDTO.builder()
                .requiredAmount(amount)
                .timestamp(timestamp)
                .build();
        consumptionService.consumeEnergy(request, user);
    }

    private void createAndSaveEnergyCosts() {
        LocalDateTime timestamp = LocalDateTime.now();
        List<EnergyCost> costs = List.of(
                createEnergyCost(EnergyType.SOLAR, 0.15, timestamp),
                createEnergyCost(EnergyType.WIND, 0.12, timestamp),
                createEnergyCost(EnergyType.HYDRO, 0.10, timestamp),
                createEnergyCost(EnergyType.BIOMASS, 0.18, timestamp),
                createEnergyCost(EnergyType.GRID, 0.20, timestamp)
        );
        costRepository.saveAll(costs);
    }

    private EnergyCost createEnergyCost(EnergyType type, double cost, LocalDateTime timestamp) {
        return EnergyCost.builder()
                .type(type)
                .cost(cost)
                .lastUpdated(timestamp)
                .build();
    }

    @RollbackExecution
    public void rollback() {
        template.remove(EnergyProduction.class).all();
        template.remove(EnergyConsumptionHistory.class).all();
        template.remove(EnergyCost.class).all();
    }
}