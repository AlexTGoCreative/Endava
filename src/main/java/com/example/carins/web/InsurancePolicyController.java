package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.web.dto.InsurancePolicyCreateDto;
import com.example.carins.web.dto.InsurancePolicyResponseDto;
import com.example.carins.web.dto.InsurancePolicyUpdateDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class InsurancePolicyController {

    private final InsurancePolicyRepository policyRepository;
    private final CarRepository carRepository;

    public InsurancePolicyController(InsurancePolicyRepository policyRepository, CarRepository carRepository) {
        this.policyRepository = policyRepository;
        this.carRepository = carRepository;
    }

    @PostMapping("/cars/{carId}/policies")
    public ResponseEntity<?> createPolicy(@PathVariable Long carId, @Valid @RequestBody InsurancePolicyCreateDto dto) {
        Optional<Car> carOpt = carRepository.findById(carId);
        if (carOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Car car = carOpt.get();
        InsurancePolicy policy = new InsurancePolicy(car, dto.provider(), dto.startDate(), dto.endDate());

        InsurancePolicy savedPolicy = policyRepository.save(policy);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPolicy.getId())
                .toUri();

        return ResponseEntity.created(location).body(
            InsurancePolicyResponseDto.fromEntity(savedPolicy)
        );
    }

    @PutMapping("/policies/{policyId}")
    public ResponseEntity<?> updatePolicy(@PathVariable Long policyId, @Valid @RequestBody InsurancePolicyUpdateDto dto) {
        Optional<InsurancePolicy> policyOpt = policyRepository.findById(policyId);
        if (policyOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        InsurancePolicy policy = policyOpt.get();
        if (dto.provider() != null) {
            policy.setProvider(dto.provider());
        }
        if (dto.startDate() != null) {
            policy.setStartDate(dto.startDate());
        }
        if (dto.endDate() != null) {
            policy.setEndDate(dto.endDate());
        }

        InsurancePolicy savedPolicy = policyRepository.save(policy);
        return ResponseEntity.ok(InsurancePolicyResponseDto.fromEntity(savedPolicy));
    }

    @GetMapping("/policies/{policyId}")
    public ResponseEntity<?> getPolicy(@PathVariable Long policyId) {
        return policyRepository.findById(policyId)
                .map(policy -> ResponseEntity.ok(InsurancePolicyResponseDto.fromEntity(policy)))
                .orElse(ResponseEntity.notFound().build());
    }
}
