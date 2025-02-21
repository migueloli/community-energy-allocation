package com.ilo.energyallocation.batch.service.interfaces;

import com.ilo.energyallocation.user.model.IloUser;
import org.springframework.web.multipart.MultipartFile;

public interface ICsvDataService {
    void processCSV(MultipartFile file, IloUser currentUser);
}
