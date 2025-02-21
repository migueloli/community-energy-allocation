package com.ilo.energyallocation.batch.service.interfaces;

import com.ilo.energyallocation.user.model.IloUser;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public interface IBatchProcessingService {
    void processBatchData(MultipartFile file, IloUser currentUser);

    void clearBatchData(LocalDateTime startDate, LocalDateTime endDate);
}
