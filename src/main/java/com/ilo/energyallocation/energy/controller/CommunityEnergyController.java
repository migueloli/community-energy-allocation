package com.ilo.energyallocation.energy.controller;

import com.ilo.energyallocation.common.exception.dto.ErrorResponse;
import com.ilo.energyallocation.energy.dto.CommunityEnergyResponseDTO;
import com.ilo.energyallocation.energy.dto.CommunityEnergyShareRequestDTO;
import com.ilo.energyallocation.energy.service.CommunityEnergyService;
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
@RequestMapping("/api/v1/community")
@Tag(name = "Community Energy", description = "APIs for managing community energy sharing")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CommunityEnergyController {
    private final CommunityEnergyService communityEnergyService;

    @Operation(
            summary = "Share energy with community pool",
            description = "Allows users to contribute their excess energy to the community pool"
    )
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "201", description = "Energy successfully shared",
                            content = @Content(schema = @Schema(implementation = CommunityEnergyResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Invalid energy amount",
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
                            responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping("/share")
    @ResponseStatus(HttpStatus.CREATED)
    public CommunityEnergyResponseDTO shareEnergy(
            @AuthenticationPrincipal IloUser user,
            @Valid @RequestBody CommunityEnergyShareRequestDTO request
    ) {
        return communityEnergyService.shareEnergy(user.getId(), request.getEnergyAmount());
    }

    @Operation(
            summary = "Get available community energy"
    )
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200", description = "Available energy retrieved successfully",
                            content = @Content(schema = @Schema(implementation = double.class))
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
                            responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/available")
    public double getAvailableEnergy() {
        return communityEnergyService.getAvailableCommunityEnergy();
    }

    @Operation(
            summary = "Get user's energy contributions"
    )
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200", description = "Contributions retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CommunityEnergyResponseDTO.class))
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
                            responseCode = "404", description = "No contributions found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/contributions")
    public List<CommunityEnergyResponseDTO> getUserContributions(@AuthenticationPrincipal IloUser user) {
        return communityEnergyService.getUserContributions(user.getId());
    }
}
