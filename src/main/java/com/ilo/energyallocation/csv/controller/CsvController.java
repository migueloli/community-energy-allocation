package com.ilo.energyallocation.csv.controller;

import com.ilo.energyallocation.common.exception.dto.ErrorResponse;
import com.ilo.energyallocation.csv.service.CsvDataService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/csv")
@Tag(name = "CSV Upload", description = "Admin API for uploading energy data via CSV")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CsvController {
    private final CsvDataService csvDataService;

    @Operation(
            summary = "Upload CSV file",
            description = "Admin endpoint to upload consumer or producer energy data CSV file"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "CSV processed successfully"),
                    @ApiResponse(
                            responseCode = "400", description = "Invalid CSV format",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Forbidden - Admin access required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Processing error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadCSV(
            @RequestParam("file") MultipartFile file,
            @Parameter(hidden = true) @AuthenticationPrincipal IloUser user
    ) {
        csvDataService.processCSV(file);
        return ResponseEntity.ok("CSV processed successfully");
    }
}
