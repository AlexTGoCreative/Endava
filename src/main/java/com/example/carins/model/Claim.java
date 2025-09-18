package com.example.carins.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim")
public class Claim {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    @NotNull(message = "Claim date is required")
    @Column(nullable = false)
    private LocalDate claimDate;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Claim() {}

    public Claim(Car car, LocalDate claimDate, String description, BigDecimal amount) {
        this.car = car;
        this.claimDate = claimDate;
        this.description = description;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and setters
    public Long getId() { return id; }
    
    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
    
    public LocalDate getClaimDate() { return claimDate; }
    public void setClaimDate(LocalDate claimDate) { this.claimDate = claimDate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}
