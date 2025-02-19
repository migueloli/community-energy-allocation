package com.ilo.energyallocation.csv.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class ConsumerData {
    @CsvBindByName(column = "series")
    private String series;

    @CsvBindByName(column = "timestamp")
    private String timestamp;

    @CsvBindByName(column = "consumption")
    private Double consumption;
}
