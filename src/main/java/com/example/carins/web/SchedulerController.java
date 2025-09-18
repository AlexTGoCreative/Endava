package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.PolicyExpirationScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/scheduler")
public class SchedulerController {
    
    @Autowired
    private PolicyExpirationScheduler policyExpirationScheduler;
    
    @Autowired
    private InsurancePolicyRepository policyRepository;
    
    @Autowired
    private CarRepository carRepository;
    
    /**
     * Manual endpoint to trigger the policy expiration check
     * Useful for testing the cron job functionality
     */
    @PostMapping("/trigger-expiration-check")
    public ResponseEntity<String> triggerExpirationCheck() {
        policyExpirationScheduler.triggerExpirationCheck();
        return ResponseEntity.ok("Expiration check triggered successfully. Check logs for results.");
    }
    
    /**
     * Test endpoint to create a policy that expires today for testing purposes
     */
    @PostMapping("/create-test-expiring-policy/{carId}")
    public ResponseEntity<String> createTestExpiringPolicy(@PathVariable Long carId) {
        Optional<Car> carOpt = carRepository.findById(carId);
        if (carOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Car with ID " + carId + " not found");
        }
        
        Car car = carOpt.get();
        LocalDate today = LocalDate.now();
        LocalDate pastDate = today.minusDays(30); // Started 30 days ago
        
        InsurancePolicy testPolicy = new InsurancePolicy(
            car, 
            "TestProvider", 
            pastDate, 
            today // Expires today
        );
        
        InsurancePolicy savedPolicy = policyRepository.save(testPolicy);
        
        return ResponseEntity.ok("Test expiring policy created with ID: " + savedPolicy.getId() + 
                                " for car " + carId + ". Expires today: " + today);
    }
}
