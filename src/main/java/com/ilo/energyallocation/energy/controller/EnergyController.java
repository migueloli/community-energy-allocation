package com.ilo.energyallocation.energy.controller;

import com.ilo.energyallocation.common.exception.dto.ErrorResponse;
import com.ilo.energyallocation.common.ratelimit.RateLimit;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionHistoryResponseDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.dto.EnergyCostResponseDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionResponseDTO;
import com.ilo.energyallocation.energy.dto.EnergySummaryResponseDTO;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.service.interfaces.IDemandCalculationService;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyConsumptionHistoryService;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyConsumptionService;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyProductionService;
import com.ilo.energyallocation.user.model.IloUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/v1/energy")
@Tag(name = "Energy Consumption", description = "APIs for managing energy consumption and production")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class EnergyController {
    private final IEnergyConsumptionService consumptionService;
    private final IEnergyConsumptionHistoryService consumptionLogService;
    private final IEnergyProductionService productionService;
    private final IDemandCalculationService demandCalculationService;

    // Add new endpoint for manual demand calculation trigger (admin only)
    @Operation(summary = "[Admin] Trigger demand calculation manually")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/demand/calculate")
    @ResponseStatus(HttpStatus.OK)
    public void triggerDemandCalculation() {
        demandCalculationService.calculateDemandAllocation();
    }

    // Add endpoint to get current energy costs
    @Operation(summary = "Get current energy costs")
    @GetMapping("/costs")
    public EnergyCostResponseDTO getCurrentEnergyCosts() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                .withMinute((LocalDateTime.now().getMinute() / 15) * 15);

        return EnergyCostResponseDTO.builder()
                .solarCost(demandCalculationService.calculateNewCost(EnergyType.SOLAR, now))
                .windCost(demandCalculationService.calculateNewCost(EnergyType.WIND, now))
                .hydroCost(demandCalculationService.calculateNewCost(EnergyType.HYDRO, now))
                .biomassCost(demandCalculationService.calculateNewCost(EnergyType.BIOMASS, now))
                .gridCost(demandCalculationService.calculateNewCost(EnergyType.GRID, now))
                .timestamp(now)
                .build();
    }

    @Operation(
            summary = "Consume energy",
            description = "Request energy consumption using optimal strategy selection"
    )
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200", description = "Energy successfully consumed",
                            content = @Content(schema = @Schema(implementation = EnergyConsumptionResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Invalid request parameters",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "422", description = "Insufficient energy available",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "429", description = "Too many requests",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @RateLimit
    @PostMapping("/consume")
    public EnergyConsumptionResponseDTO consumeEnergy(
            @AuthenticationPrincipal IloUser user,
            @Valid @RequestBody EnergyConsumptionRequestDTO request
    ) {
        return consumptionService.consumeEnergy(request, user);
    }

    @Operation(
            summary = "Get consumption history by date range"
    )
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200", description = "Filtered history retrieved successfully",
                            content = @Content(
                                    schema = @Schema(implementation = EnergyConsumptionHistoryResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "No history found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "429", description = "Too many requests",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @RateLimit
    @GetMapping("/consume/history")
    public List<EnergyConsumptionHistoryResponseDTO> getConsumptionHistory(
            @AuthenticationPrincipal IloUser user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return consumptionLogService.getUserLogsByPeriod(user.getId(), startDate, endDate);
    }

    @Operation(summary = "Get energy summary by period")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200", description = "Summary retrieved successfully",
                            content = @Content(schema = @Schema(implementation = EnergySummaryResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Invalid date range",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @RateLimit
    @GetMapping("/consume/summary")
    public EnergySummaryResponseDTO getUserSummary(
            @AuthenticationPrincipal IloUser user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return consumptionService.getEnergySummary(user.getId(), startDate, endDate);
    }

    @Operation(summary = "[Admin] Get energy summary by user ID and period")
    @PreAuthorize("hasRole('ADMIN')")
    @RateLimit
    @GetMapping("/consume/{userId}/summary")
    public EnergySummaryResponseDTO getUserSummaryById(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return consumptionService.getEnergySummary(userId, startDate, endDate);
    }

    @Operation(
            summary = "Log energy production",
            description = "Records new energy production from various sources"
    )
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "201", description = "Production logged successfully",
                            content = @Content(schema = @Schema(implementation = EnergyProductionResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Invalid production data",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "422", description = "Invalid energy values",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "429", description = "Too many requests",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @RateLimit
    @PostMapping("/production")
    @ResponseStatus(HttpStatus.CREATED)
    public EnergyProductionResponseDTO logProduction(
            @AuthenticationPrincipal IloUser user,
            @Valid @RequestBody EnergyProductionRequestDTO request
    ) {
        return productionService.logProduction(user.getId(), request);
    }

    @Operation(
            summary = "Get production history"
    )
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200", description = "Production history retrieved",
                            content = @Content(schema = @Schema(implementation = EnergyProductionResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Invalid production data",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "No production history found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "429", description = "Too many requests",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @RateLimit
    @GetMapping("/production")
    public List<EnergyProductionResponseDTO> getProductionHistory(@AuthenticationPrincipal IloUser user) {
        return productionService.getProductionHistory(user.getId());
    }
}
