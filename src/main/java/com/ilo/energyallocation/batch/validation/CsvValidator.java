package com.ilo.energyallocation.batch.validation;

import com.ilo.energyallocation.batch.exception.CsvProcessingException;
import com.ilo.energyallocation.energy.model.EnergyType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CsvValidator {
    private static final String ALLOWED_CONTENT_TYPE = "text/csv";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> VALID_ENERGY_TYPES = Arrays.stream(EnergyType.values())
            .map(type -> type.name().toLowerCase())
            .collect(Collectors.toSet());

    public void validateCsvFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CsvProcessingException("File is empty");
        }

        if (!ALLOWED_CONTENT_TYPE.equals(file.getContentType())) {
            throw new CsvProcessingException("Invalid file type. Only CSV files are allowed");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CsvProcessingException("File size exceeds maximum limit of 10MB");
        }
    }
}
