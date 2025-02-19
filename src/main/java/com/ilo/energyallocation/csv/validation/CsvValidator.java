package com.ilo.energyallocation.csv.validation;

import com.ilo.energyallocation.csv.exception.CsvProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class CsvValidator {
    private static final String ALLOWED_CONTENT_TYPE = "text/csv";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

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

    public void validateDataRow(String[] row, String[] headers) {
        if (row.length != headers.length) {
            throw new CsvProcessingException("Invalid row format. Expected " + headers.length + " columns");
        }
    }
}
