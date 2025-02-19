package com.ilo.energyallocation.billing.service.interfaces;

import com.ilo.energyallocation.billing.dto.BillingSummaryRequestDTO;
import com.ilo.energyallocation.billing.dto.BillingSummaryResponseDTO;

public interface IBillingService {
    BillingSummaryResponseDTO generateBillingSummary(String userId, BillingSummaryRequestDTO billingSummaryRequest);
}
