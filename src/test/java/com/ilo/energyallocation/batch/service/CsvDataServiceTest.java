package com.ilo.energyallocation.batch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import com.ilo.energyallocation.batch.exception.CsvProcessingException;
import com.ilo.energyallocation.batch.validation.CsvValidator;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyConsumptionService;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyProductionService;
import com.ilo.energyallocation.user.model.EnergyPreference;
import com.ilo.energyallocation.user.model.IloUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class CsvDataServiceTest {
    @Mock
    private IEnergyConsumptionService consumptionService;
    @Mock
    private IEnergyProductionService productionService;
    @Mock
    private CsvValidator csvValidator;

    @InjectMocks
    private CsvDataService csvDataService;

    @Test
    void processCSV_WithConsumerData_ShouldProcessSuccessfully() throws IOException {
        // Given
        var user = createTestUser();
        byte[] testData = getTestConsumerData().getBytes();
        MultipartFile mockFile = new MockMultipartFile("file", "consumer.csv", "text/csv", testData);

        doNothing().when(csvValidator).validateCsvFile(mockFile);

        // When
        csvDataService.processCSV(mockFile, user);

        // Then
        verify(consumptionService, times(5)).consumeEnergy(any(), any()); // Expecting 5 calls based on CSV data
    }

    @Test
    void processCSV_WithProducerData_ShouldProcessSuccessfully() throws IOException {
        // Given
        var user = createTestUser();
        byte[] testData = getTestProducerData().getBytes();
        MultipartFile mockFile = new MockMultipartFile("file", "producer.csv", "text/csv", testData);

        doNothing().when(csvValidator).validateCsvFile(mockFile);

        // When
        csvDataService.processCSV(mockFile, user);

        // Then
        verify(productionService, times(5)).logProduction(any(), any()); // Expecting 5 calls based on CSV data
    }

    @Test
    void processCSV_WithEmptyFile_ShouldThrowCsvProcessingException() {
        // Given
        var user = createTestUser();
        MultipartFile mockFile = new MockMultipartFile("file", "empty.csv", "text/csv", new byte[0]);

        doThrow(new CsvProcessingException("File is empty")).when(csvValidator).validateCsvFile(mockFile);

        // When/Then
        CsvProcessingException exception = assertThrows(
                CsvProcessingException.class, () ->
                        csvDataService.processCSV(mockFile, user)
        );

        assertEquals("File is empty", exception.getMessage());
    }

    private IloUser createTestUser() {
        return IloUser.builder()
                .id("testId")
                .username("testUser")
                .preference(EnergyPreference.SOLAR)
                .build();
    }

    private String getTestConsumerData() {
        return """
                DocumentIdentification;_000305826914;_000305826915;_000305826915
                SenderIdentification;_9910015000005;293;
                ReceiverIdentification;_INTERN7;293;
                CreationDateTime;03.09.2024 05:00:00 +00:00;;
                ;;;
                Location;51124784179;Ansprechpartner;Ansprechpartner
                Series;1-1:1.29.0:SRW;Kontaktdetail;030 1234567890
                Success;0;Kontaktart;Telefon
                Reason;<Reason why not successfull>;;
                From;To;51124784179;
                2024-09-01T22:00:00.0000000Z;2024-09-01T22:15:00.0000000Z;0,036;
                2024-09-01T22:15:00.0000000Z;2024-09-01T22:30:00.0000000Z;0,023;
                2024-09-01T22:30:00.0000000Z;2024-09-01T22:45:00.0000000Z;0,021;
                2024-09-01T22:45:00.0000000Z;2024-09-01T23:00:00.0000000Z;0,024;
                2024-09-01T23:00:00.0000000Z;2024-09-01T23:15:00.0000000Z;0,018;
                """;
    }

    private String getTestProducerData() {
        return """
                DocumentIdentification;_000305826914;_000305826915;_000305826915
                SenderIdentification;_9910015000005;293;
                ReceiverIdentification;_INTERN7;293;
                CreationDateTime;03.09.2024 05:00:00 +00:00;;
                ;;;
                Location;41124784179;Ansprechpartner;Ansprechpartner
                Series;1-1:2.29.0:SRW;Kontaktdetail;030 1234567890
                Success;0;Kontaktart;Telefon
                Reason;<Reason why not successfull>;;
                From;To;51124784179;
                2024-09-01T22:00:00.0000000Z;2024-09-01T22:15:00.0000000Z;0,036;
                2024-09-01T22:15:00.0000000Z;2024-09-01T22:30:00.0000000Z;0,34;
                2024-09-01T22:30:00.0000000Z;2024-09-01T22:45:00.0000000Z;0,056;
                2024-09-01T22:45:00.0000000Z;2024-09-01T23:00:00.0000000Z;0,024;
                2024-09-01T23:00:00.0000000Z;2024-09-01T23:15:00.0000000Z;0,018;
                """;
    }
}