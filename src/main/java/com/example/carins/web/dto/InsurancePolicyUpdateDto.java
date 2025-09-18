package com.example.carins.web.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record InsurancePolicyUpdateDto(
    String provider,
    LocalDate startDate,

    @NotNull(message = "End date is required")
    LocalDate endDate
) {}
