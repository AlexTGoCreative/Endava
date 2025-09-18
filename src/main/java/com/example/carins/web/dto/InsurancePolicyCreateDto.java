package com.example.carins.web.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record InsurancePolicyCreateDto(
    String provider,

    @NotNull(message = "Start date is required")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    LocalDate endDate
) {}
