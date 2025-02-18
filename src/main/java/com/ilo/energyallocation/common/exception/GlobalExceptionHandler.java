package com.ilo.energyallocation.common.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Tag(name = "Error Handling", description = "Global error handling for the API")
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    @ApiResponse(
            responseCode = "4xx",
            description = "Client error",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        final ErrorResponse errorResponse = new ErrorResponse(ex.getStatus().value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        final ErrorResponse errorResponse = new ErrorResponse(500, "An unexpected error occurred");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Schema(name = "ErrorResponse", description = "Standard error response structure")
    public record ErrorResponse(
            @Schema(description = "HTTP status code", example = "400") int status,
            @Schema(description = "Error message details", example = "Invalid input provided") String message
    ) {
    }
}
