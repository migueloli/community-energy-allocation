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
import java.util.stream.IntStream;

@RequiredArgsConstructor
@ChangeUnit(id = "energyDataMigration001", order = "002", author = "miguel", systemVersion = "1")
public class EnergyDataMigration {
    static final int weekCount = 6;  // Number of weeks to create data for
    private final UserRepository userRepository;
    private final IEnergyProductionService productionService;
    private final IEnergyConsumptionService consumptionService;
    private final EnergyCostRepository costRepository;
    private final MongoTemplate template;

    @Execution
    public void addInitialEnergyData() {
        createAndSaveEnergyCosts();
        List<IloUser> users = userRepository.findAll();
        LocalDateTime baseTime = LocalDateTime.now().minusWeeks(weekCount);

        users.forEach(user -> {
            // Create production data for each user
            IntStream.range(0, weekCount * 7).forEach(day -> {
                IntStream.range(0, 24).filter(hour -> hour % 4 == 0).forEach(hour -> {
                    LocalDateTime timestamp = baseTime.plusDays(day).plusHours(hour);
                    createDiversifiedProduction(user.getId(), timestamp);
                });
            });

            // Create consumption data for each user
            IntStream.range(0, weekCount * 7).forEach(day -> {
                IntStream.range(0, 24).filter(hour -> hour % 2 == 0).forEach(hour -> {
                    LocalDateTime timestamp = baseTime.plusDays(day).plusHours(hour);
                    double consumption = generateConsumptionAmount(hour, day % 7);
                    createAndSaveConsumption(user, consumption, timestamp);
                });
            });
        });
    }

    private double generateConsumptionAmount(int hour, int dayOfWeek) {
        Random random = new Random();
        boolean isPeakHour = (hour >= 7 && hour <= 9) || (hour >= 18 && hour <= 20);
        boolean isWeekday = dayOfWeek < 5;

        double baseAmount = 10.0;
        double variation = 20.0;

        if (isPeakHour && isWeekday) {
            baseAmount = 30.0;
            variation = 40.0;
        }

        return baseAmount + random.nextDouble() * variation;
    }

    private void createDiversifiedProduction(String userId, LocalDateTime timestamp) {
        Arrays.stream(EnergyType.values())
                .filter(type -> type != EnergyType.GRID)
                .forEach(type -> {
                    double amount = generateCustomAmount(type, timestamp.getHour());
                    if (amount > 0) {
                        createAndSaveProduction(userId, type, amount, timestamp);
                    }
                });
    }

    private double generateCustomAmount(EnergyType type, int hour) {
        Random random = new Random();
        return switch (type) {
            case SOLAR -> (hour >= 6 && hour <= 18) ? 50 + random.nextDouble() * 50 : 5;
            case WIND -> 30 + random.nextDouble() * 40;
            case HYDRO -> 40 + random.nextDouble() * 30;
            case BIOMASS -> 20 + random.nextDouble() * 30;
            default -> 0;
        };
    }

    private void createAndSaveProduction(String userId, EnergyType type, double amount, LocalDateTime timestamp) {
        EnergyProductionRequestDTO request = EnergyProductionRequestDTO.builder()
                .energyType(type)
                .production(amount)
                .timestamp(timestamp)
                .build();
        productionService.logProduction(userId, request);
    }

    private void createAndSaveConsumption(IloUser user, double amount, LocalDateTime timestamp) {
        EnergyConsumptionRequestDTO request = EnergyConsumptionRequestDTO.builder()
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