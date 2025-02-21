package com.ilo.energyallocation.batch.controller;

import com.ilo.energyallocation.batch.service.BatchProcessingService;
import com.ilo.energyallocation.common.exception.dto.ErrorResponse;
import com.ilo.energyallocation.common.ratelimit.RateLimit;
import com.ilo.energyallocation.user.model.IloUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/data")
@Tag(name = "Energy Data Processing", description = "APIs for processing energy data via CSV files")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class BatchController {
    private final BatchProcessingService batchProcessingService;

    @Operation(
            summary = "Upload batch data",
            description = "Process a week's worth of energy data from CSV file"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "Data processed successfully"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid data format",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Admin access required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping("/upload")
    @RateLimit
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadData(
            @RequestParam("file") MultipartFile file,
            @Parameter(hidden = true) @AuthenticationPrincipal IloUser user
    ) {
        batchProcessingService.processBatchData(file, user);
        return ResponseEntity.ok("Data processed successfully");
    }

    @Operation(summary = "Clear energy data for a specific period")
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> clearData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        batchProcessingService.clearBatchData(startDate, endDate);
        return ResponseEntity.ok("Data cleared successfully");
    }
}