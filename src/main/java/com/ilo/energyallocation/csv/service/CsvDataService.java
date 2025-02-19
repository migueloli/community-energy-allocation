package com.ilo.energyallocation.csv.service;

import com.ilo.energyallocation.csv.exception.CsvProcessingException;
import com.ilo.energyallocation.csv.model.ConsumerData;
import com.ilo.energyallocation.csv.model.ProducerData;
import com.ilo.energyallocation.csv.validation.CsvValidator;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyProducedDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionRequestDTO;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.service.EnergyConsumptionService;
import com.ilo.energyallocation.energy.service.EnergyProductionService;
import com.ilo.energyallocation.user.model.IloUser;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvDataService {
    private final EnergyConsumptionService consumptionService;
    private final EnergyProductionService productionService;
    private final CsvValidator csvValidator;

    public void processCSV(MultipartFile file) {
        csvValidator.validateCsvFile(file);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String[] headers = validateAndGetHeaders(reader);
            String firstDataRow = validateAndGetFirstDataRow(reader);

            if (isConsumerData(firstDataRow)) {
                List<ConsumerData> consumerData = parseCsv(reader, ConsumerData.class);
                validateConsumerData(consumerData);
                processConsumerData(consumerData);
            } else if (isProducerData(firstDataRow)) {
                List<ProducerData> producerData = parseCsv(reader, ProducerData.class);
                validateProducerData(producerData);
                processProducerData(producerData);
            } else {
                throw new CsvProcessingException("Unknown CSV format");
            }
        } catch (IOException e) {
            throw new CsvProcessingException("Error processing CSV: " + e.getMessage());
        }
    }

    private void processConsumerData(List<ConsumerData> consumerData) {
        consumerData.forEach(data -> {
            EnergyConsumptionRequestDTO request = EnergyConsumptionRequestDTO.builder()
                    .requiredAmount(data.getConsumption())
                    .timestamp(parseTimestamp(data.getTimestamp()))
                    .build();

            consumptionService.consumeEnergy(request, getCurrentUser());
        });
    }

    private void processProducerData(List<ProducerData> producerData) {
        producerData.forEach(data -> {
            EnergyProducedDTO producedEnergy = mapToProducedEnergy(data);
            EnergyProductionRequestDTO request = EnergyProductionRequestDTO.builder()
                    .producedEnergy(producedEnergy)
                    .timestamp(parseTimestamp(data.getTimestamp()))
                    .build();

            productionService.logProduction(getCurrentUser().getId(), request);
        });
    }

    private String[] validateAndGetHeaders(BufferedReader reader) throws IOException {
        String headerLine = reader.readLine();
        if (headerLine == null) {
            throw new CsvProcessingException("CSV file is empty");
        }
        return headerLine.split(",");
    }

    private String validateAndGetFirstDataRow(BufferedReader reader) throws IOException {
        String firstDataRow = reader.readLine();
        if (firstDataRow == null) {
            throw new CsvProcessingException("CSV file contains only headers");
        }
        return firstDataRow;
    }

    private void validateConsumerData(List<ConsumerData> consumerData) {
        consumerData.forEach(data -> {
            if (data.getConsumption() == null || data.getConsumption() <= 0) {
                throw new CsvProcessingException("Invalid consumption value: " + data.getConsumption());
            }
            if (isInvalidTimestamp(data.getTimestamp())) {
                throw new CsvProcessingException("Invalid timestamp format: " + data.getTimestamp());
            }
            if (!isValidConsumerSeries(data.getSeries())) {
                throw new CsvProcessingException("Invalid consumer series format: " + data.getSeries());
            }
        });
    }

    private void validateProducerData(List<ProducerData> producerData) {
        producerData.forEach(data -> {
            if (data.getProduction() == null || data.getProduction() <= 0) {
                throw new CsvProcessingException("Invalid production value: " + data.getProduction());
            }
            if (isInvalidTimestamp(data.getTimestamp())) {
                throw new CsvProcessingException("Invalid timestamp format: " + data.getTimestamp());
            }
            if (!isValidProducerSeries(data.getSeries())) {
                throw new CsvProcessingException("Invalid producer series format: " + data.getSeries());
            }
            if (!isValidEnergyType(data.getEnergyType())) {
                throw new CsvProcessingException("Invalid energy type: " + data.getEnergyType());
            }
        });
    }

    private boolean isConsumerData(String row) {
        return row.contains(":1.29");
    }

    private boolean isProducerData(String row) {
        return row.contains(":2.29");
    }

    private boolean isInvalidTimestamp(String timestamp) {
        try {
            LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return false;
        } catch (DateTimeParseException e) {
            return true;
        }
    }

    private boolean isValidConsumerSeries(String series) {
        return series != null && series.matches("\\d+-\\d+:1\\.29");
    }

    private boolean isValidProducerSeries(String series) {
        return series != null && series.matches("\\d+-\\d+:2\\.29");
    }

    private boolean isValidEnergyType(String energyType) {
        try {
            EnergyType.valueOf(energyType.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private LocalDateTime parseTimestamp(String timestamp) {
        return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private EnergyProducedDTO mapToProducedEnergy(ProducerData data) {
        return EnergyProducedDTO.builder()
                .solar(getEnergyValueByType(data, EnergyType.SOLAR))
                .wind(getEnergyValueByType(data, EnergyType.WIND))
                .hydro(getEnergyValueByType(data, EnergyType.HYDRO))
                .biomass(getEnergyValueByType(data, EnergyType.BIOMASS))
                .build();
    }

    private double getEnergyValueByType(ProducerData data, EnergyType type) {
        return type.name().equalsIgnoreCase(data.getEnergyType()) ? data.getProduction() : 0.0;
    }

    private IloUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (IloUser) authentication.getPrincipal();
    }

    private <T> List<T> parseCsv(BufferedReader reader, Class<T> type) {
        return new CsvToBeanBuilder<T>(reader)
                .withType(type)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse();
    }
}
