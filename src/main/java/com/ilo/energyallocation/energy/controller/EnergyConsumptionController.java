package com.ilo.energyallocation.energy.controller;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionHistoryRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionHistoryResponseDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.service.EnergyConsumptionService;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyConsumptionHistoryService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/energy/consume")
@Tag(name = "Energy Consumption", description = "APIs for managing energy consumption")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class EnergyConsumptionController {
    private final EnergyConsumptionService consumptionService;
    private final IEnergyConsumptionHistoryService consumptionLogService;

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
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "422", description = "Insufficient energy available"),
                    @ApiResponse(responseCode = "429", description = "Too many requests"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping
    public EnergyConsumptionResponseDTO consumeEnergy(
            @AuthenticationPrincipal IloUser user,
            @Valid @RequestBody EnergyConsumptionRequestDTO request
    ) {
        return consumptionService.consumeEnergy(request.getRequiredAmount(), user);
    }

    @Operation(
            summary = "Get consumption history"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "No history found"),
                    @ApiResponse(responseCode = "429", description = "Too many requests"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/history")
    public List<EnergyConsumptionHistoryResponseDTO> getConsumptionHistory(@AuthenticationPrincipal IloUser user) {
        return consumptionLogService.getUserLogs(user.getId());
    }

    @Operation(
            summary = "Get consumption history by date range"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Filtered history retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid date range"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "No history found for date range"),
                    @ApiResponse(responseCode = "429", description = "Too many requests"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/history/range")
    public List<EnergyConsumptionHistoryResponseDTO> getConsumptionHistoryByDateRange(
            @AuthenticationPrincipal IloUser user,
            @Valid @RequestBody EnergyConsumptionHistoryRequestDTO request
    ) {
        return consumptionLogService.getUserLogsByPeriod(user.getId(), request);
    }
}
