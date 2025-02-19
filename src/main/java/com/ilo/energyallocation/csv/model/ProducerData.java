package com.ilo.energyallocation.csv.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class ProducerData {
    @CsvBindByName(column = "series")
    private String series;

    @CsvBindByName(column = "timestamp")
    private String timestamp;

    @CsvBindByName(column = "production")
    private Double production;

    @CsvBindByName(column = "type")
    private String energyType;
}
