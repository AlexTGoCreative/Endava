package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.Claim;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.ClaimRepository;
import com.example.carins.web.dto.ClaimCreateDto;
import com.example.carins.web.dto.ClaimResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final CarRepository carRepository;

    public ClaimService(ClaimRepository claimRepository, CarRepository carRepository) {
        this.claimRepository = claimRepository;
        this.carRepository = carRepository;
    }

    public ClaimResponseDto createClaim(Long carId, ClaimCreateDto claimDto) {
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new CarNotFoundException("Car with id " + carId + " not found"));

        Claim claim = new Claim(car, claimDto.claimDate(), claimDto.description(), claimDto.amount());
        Claim savedClaim = claimRepository.save(claim);
        
        return toResponseDto(savedClaim);
    }

    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getClaimsByCarId(Long carId) {
        // Verify car exists
        if (!carRepository.existsById(carId)) {
            throw new CarNotFoundException("Car with id " + carId + " not found");
        }
        
        return claimRepository.findByCarIdOrderByClaimDateDesc(carId)
            .stream()
            .map(this::toResponseDto)
            .toList();
    }

    private ClaimResponseDto toResponseDto(Claim claim) {
        return new ClaimResponseDto(
            claim.getId(),
            claim.getCar().getId(),
            claim.getClaimDate(),
            claim.getDescription(),
            claim.getAmount(),
            claim.getCreatedAt()
        );
    }

    public static class CarNotFoundException extends RuntimeException {
        public CarNotFoundException(String message) {
            super(message);
        }
    }
}
