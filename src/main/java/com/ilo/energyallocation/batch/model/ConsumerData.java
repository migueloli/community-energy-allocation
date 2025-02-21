package com.ilo.energyallocation.batch.model;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class ConsumerData {
    @CsvBindByPosition(position = 0)
    private String startTimestamp;

    @CsvBindByPosition(position = 1)
    private String endTimestamp;

    @CsvBindByPosition(position = 2)
    private String value;
}
