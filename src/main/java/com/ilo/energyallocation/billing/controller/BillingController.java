package com.ilo.energyallocation.billing.controller;

import com.ilo.energyallocation.billing.dto.BillingSummaryRequestDTO;
import com.ilo.energyallocation.billing.dto.BillingSummaryResponseDTO;
import com.ilo.energyallocation.billing.service.BillingService;
import com.ilo.energyallocation.common.exception.dto.ErrorResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/billing")
@Tag(name = "Billing", description = "APIs for energy consumption billing and cost analysis")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class BillingController {
    private final BillingService billingService;

    @Operation(
            summary = "Get billing summary",
            description = "Retrieves detailed billing summary including costs per energy source"
    )
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200", description = "Billing summary retrieved successfully",
                            content = @Content(schema = @Schema(implementation = BillingSummaryResponseDTO.class))
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
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "No billing data found",
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
    @GetMapping("/summary")
    public BillingSummaryResponseDTO getBillingSummary(
            @AuthenticationPrincipal IloUser user,
            @Valid @RequestBody BillingSummaryRequestDTO request
    ) {
        return billingService.generateBillingSummary(user.getId(), request);
    }
}
