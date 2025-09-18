package com.example.carins.web.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public record CarHistoryEventDto(
    String eventType, // "INSURANCE_POLICY", "CLAIM", "OWNERSHIP_CHANGE"
    LocalDate eventDate,
    LocalDateTime createdAt,
    String description,
    BigDecimal amount, // null for non-claim events
    String provider, // for insurance policies
    String details
) {}
