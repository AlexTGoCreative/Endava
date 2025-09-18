package com.example.carins.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ClaimCreateDto(
    @NotNull(message = "Claim date is required")
    LocalDate claimDate,
    
    @NotBlank(message = "Description is required")
    String description,
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    BigDecimal amount
) {}
