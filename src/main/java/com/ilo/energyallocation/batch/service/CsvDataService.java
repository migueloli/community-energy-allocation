package com.ilo.energyallocation.batch.service;

import com.ilo.energyallocation.batch.exception.CsvProcessingException;
import com.ilo.energyallocation.batch.model.ConsumerData;
import com.ilo.energyallocation.batch.model.ProducerData;
import com.ilo.energyallocation.batch.service.interfaces.ICsvDataService;
import com.ilo.energyallocation.batch.validation.CsvValidator;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionRequestDTO;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyConsumptionService;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyProductionService;
import com.ilo.energyallocation.user.model.IloUser;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvDataService implements ICsvDataService {
    private final IEnergyConsumptionService consumptionService;
    private final IEnergyProductionService productionService;
    private final CsvValidator csvValidator;

    @Override
    public void processCSV(MultipartFile file, IloUser currentUser) {
        csvValidator.validateCsvFile(file);

        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            // Read first 10 lines for metadata and determine type
            List<String> headerLines = reader.lines().limit(10).collect(Collectors.toList());
            boolean isConsumerFile = determineFileType(headerLines);

            // Reset input stream for full parsing
            InputStream resetInputStream = file.getInputStream();
            BufferedReader newReader = new BufferedReader(new InputStreamReader(resetInputStream));

            CsvToBean<?> csvBean;
            if (isConsumerFile) {
                csvBean = new CsvToBeanBuilder<ConsumerData>(newReader)
                        .withType(ConsumerData.class)
                        .withSeparator(';')
                        .withIgnoreLeadingWhiteSpace(true)
                        .withIgnoreEmptyLine(true)
                        .build();
                processConsumerData((List<ConsumerData>) csvBean.parse(), currentUser);
            } else {
                csvBean = new CsvToBeanBuilder<ProducerData>(newReader)
                        .withType(ProducerData.class)
                        .withSeparator(';')
                        .withIgnoreLeadingWhiteSpace(true)
                        .withIgnoreEmptyLine(true)
                        .build();
                processProducerData((List<ProducerData>) csvBean.parse(), currentUser);
            }

        } catch (Exception e) {
            throw new CsvProcessingException("Error processing CSV: " + e.getMessage());
        }
    }

    /**
     * Determines if the file is a consumer or producer data file.
     */
    private boolean determineFileType(List<String> lines) {
        return lines.stream().anyMatch(line -> line.contains("Series") && line.contains("1-1:1.29"));
    }

    /**
     * Processes consumer energy data.
     */
    private void processConsumerData(List<ConsumerData> data, IloUser currentUser) {
        data.forEach(record -> {
            try {
                if (isInvalidConsumerRecord(record)) {
                    log.warn("Skipping invalid consumer record: {}", record);
                    return;
                }

                EnergyConsumptionRequestDTO request = EnergyConsumptionRequestDTO.builder()
                        .requiredAmount(Double.parseDouble(record.getValue().replace(",", ".")))
                        .timestamp(parseTimestamp(record.getEndTimestamp()))
                        .build();

                log.debug("Submitting consumption request: {}", request);
                consumptionService.consumeEnergy(request, currentUser);
            } catch (Exception e) {
                log.error("Error processing consumer data: {}", e.getMessage(), e);
            }
        });
    }

    /**
     * Processes producer energy data.
     */
    private void processProducerData(List<ProducerData> data, IloUser currentUser) {
        data.forEach(record -> {
            try {
                if (isInvalidProducerRecord(record)) {
                    log.warn("Skipping invalid producer record: {}", record);
                    return;
                }

                EnergyProductionRequestDTO request = EnergyProductionRequestDTO.builder()
                        .energyType(determineEnergyType(record))
                        .production(Double.parseDouble(record.getValue().replace(",", ".")))
                        .timestamp(parseTimestamp(record.getEndTimestamp()))
                        .build();

                log.debug("Submitting production request: {}", request);
                productionService.logProduction(currentUser.getId(), request);
            } catch (Exception e) {
                log.error("Error processing producer data: {}", e.getMessage(), e);
            }
        });
    }

    /**
     * Determines the energy type based on CSV data.
     */
    private EnergyType determineEnergyType(ProducerData data) {
        return EnergyType.SOLAR; // Default
    }

    /**
     * Parses a timestamp string into LocalDateTime.
     */
    private LocalDateTime parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.trim().isEmpty()) {
            throw new CsvProcessingException("Invalid or missing timestamp");
        }
        try {
            return LocalDateTime.parse(
                    timestamp.replace(".0000000Z", "Z"),
                    DateTimeFormatter.ISO_DATE_TIME
            );
        } catch (Exception e) {
            throw new CsvProcessingException("Invalid timestamp format: " + timestamp);
        }
    }

    /**
     * Validates if a consumer record is valid.
     */
    private boolean isInvalidConsumerRecord(ConsumerData record) {
        try {
            var timestamp = parseTimestamp(record.getEndTimestamp());
            var value = Double.parseDouble(record.getValue().replace(",", "."));
            return timestamp == null || value <= 0;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Validates if a producer record is valid.
     */
    private boolean isInvalidProducerRecord(ProducerData record) {
        try {
            var timestamp = parseTimestamp(record.getEndTimestamp());
            var value = Double.parseDouble(record.getValue().replace(",", "."));
            return timestamp == null || value <= 0;
        } catch (Exception e) {
            return true;
        }
    }
}