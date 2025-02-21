package com.ilo.energyallocation.batch.exception;

public class CsvProcessingException extends RuntimeException {
    public CsvProcessingException(String message) {
        super(message);
    }
}