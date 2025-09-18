package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.Claim;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.ClaimRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.ClaimService.CarNotFoundException;
import com.example.carins.web.dto.CarHistoryEventDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CarHistoryService {

    private final CarRepository carRepository;
    private final ClaimRepository claimRepository;
    private final InsurancePolicyRepository policyRepository;

    public CarHistoryService(CarRepository carRepository, 
                           ClaimRepository claimRepository, 
                           InsurancePolicyRepository policyRepository) {
        this.carRepository = carRepository;
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
    }

    public List<CarHistoryEventDto> getCarHistory(Long carId) {
        // Verify car exists
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new CarNotFoundException("Car with id " + carId + " not found"));

        List<CarHistoryEventDto> events = new ArrayList<>();

        // Add insurance policies
        List<InsurancePolicy> policies = policyRepository.findByCarIdOrderByStartDateAsc(carId);
        for (InsurancePolicy policy : policies) {
            events.add(new CarHistoryEventDto(
                "INSURANCE_POLICY",
                policy.getStartDate(),
                LocalDateTime.now(), // We don't have creation timestamp for policies
                "Insurance policy created",
                null,
                policy.getProvider(),
                String.format("Policy from %s to %s with %s", 
                    policy.getStartDate(), 
                    policy.getEndDate(), 
                    policy.getProvider() != null ? policy.getProvider() : "Unknown Provider")
            ));
        }

        // Add claims
        List<Claim> claims = claimRepository.findByCarIdOrderByClaimDateAsc(carId);
        for (Claim claim : claims) {
            events.add(new CarHistoryEventDto(
                "CLAIM",
                claim.getClaimDate(),
                claim.getCreatedAt(),
                claim.getDescription(),
                claim.getAmount(),
                null,
                String.format("Claim for %s on %s", 
                    claim.getAmount(), 
                    claim.getClaimDate())
            ));
        }

        // Sort events chronologically (by event date, then by created date)
        events.sort(Comparator
            .comparing(CarHistoryEventDto::eventDate)
            .thenComparing(CarHistoryEventDto::createdAt));

        return events;
    }
}
