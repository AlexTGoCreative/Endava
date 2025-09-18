package com.example.carins.web.dto;

import com.example.carins.model.InsurancePolicy;
import java.time.LocalDate;

public record InsurancePolicyResponseDto(
    Long id,
    Long carId,
    String carVin,
    String provider,
    LocalDate startDate,
    LocalDate endDate
) {
    public static InsurancePolicyResponseDto fromEntity(InsurancePolicy policy) {
        return new InsurancePolicyResponseDto(
            policy.getId(),
            policy.getCar().getId(),
            policy.getCar().getVin(),
            policy.getProvider(),
            policy.getStartDate(),
            policy.getEndDate()
        );
    }
}
