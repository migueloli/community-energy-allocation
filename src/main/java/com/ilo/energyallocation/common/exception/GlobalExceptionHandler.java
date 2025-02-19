package com.ilo.energyallocation.common.exception;

import com.ilo.energyallocation.common.exception.dto.ErrorResponse;
import com.ilo.energyallocation.common.exception.dto.FieldValidationError;
import com.ilo.energyallocation.common.exception.dto.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Tag(name = "Error Handling", description = "Global error handling for the API")
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    @ApiResponse(
            responseCode = "4xx", description = "Business logic error", content = @Content(
            mediaType =
                    "application/json", schema = @Schema(implementation = ErrorResponse.class)
    )
    )
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getStatus().value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(
            responseCode = "400", description = "Validation error", content = @Content(
            mediaType =
                    "application" + "/json", schema = @Schema(implementation = ValidationErrorResponse.class)
    )
    )
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldValidationError> fieldErrors =
                ex.getBindingResult().getFieldErrors().stream().map(
                        error -> new FieldValidationError(error.getField(), error.getDefaultMessage())).collect(
                        Collectors.toList());

        ValidationErrorResponse response = new ValidationErrorResponse(HttpStatus.BAD_REQUEST.value(), fieldErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ApiResponse(
            responseCode = "500", description = "Server error", content = @Content(
            mediaType = "application/json"
            , schema = @Schema(implementation = ErrorResponse.class)
    )
    )
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected " +
                "error occurred"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
