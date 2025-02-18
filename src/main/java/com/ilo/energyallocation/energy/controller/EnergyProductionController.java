package com.ilo.energyallocation.energy.controller;

import com.ilo.energyallocation.energy.dto.EnergyProductionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionResponseDTO;
import com.ilo.energyallocation.energy.service.EnergyProductionService;
import com.ilo.energyallocation.user.model.IloUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/energy/production")
@Tag(name = "Energy Production", description = "APIs for managing energy production")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class EnergyProductionController {
    private final EnergyProductionService productionService;

    @Operation(
            summary = "Log energy production",
            description = "Records new energy production from various sources"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "201", description = "Production logged successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid production data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "422", description = "Invalid energy values"),
                    @ApiResponse(responseCode = "429", description = "Too many requests"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping
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
                    @ApiResponse(responseCode = "200", description = "Production history retrieved"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "No production history found"),
                    @ApiResponse(responseCode = "429", description = "Too many requests"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping
    public List<EnergyProductionResponseDTO> getProductionHistory(@AuthenticationPrincipal IloUser user) {
        return productionService.getProductionHistory(user.getId());
    }
}