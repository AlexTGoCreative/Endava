package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) {
        if (carId == null || date == null) {
            throw new IllegalArgumentException("Car ID and date cannot be null");
        }
        
        // Validate that the car exists
        if (!carRepository.existsById(carId)) {
            throw new CarNotFoundException("Car with ID " + carId + " not found");
        }
        
        return policyRepository.existsActiveOnDate(carId, date);
    }
    
    public static class CarNotFoundException extends RuntimeException {
        public CarNotFoundException(String message) {
            super(message);
        }
    }
}
