package com.example.carins.service;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PolicyExpirationScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(PolicyExpirationScheduler.class);
    
    @Autowired
    private InsurancePolicyRepository policyRepository;
    
    // In-memory set to track policies we've already logged
    private final Set<Long> loggedExpiredPolicies = new HashSet<>();
    
    @PostConstruct
    public void init() {
        logger.info("PolicyExpirationScheduler initialized - will check for expired policies every 10 minutes");
    }
    
    /**
     * Runs every 10 minutes to check for expired policies.
     * Logs expired policies within 1 hour of their expiration (at midnight).
     */
    @Scheduled(fixedRate = 600000) // 10 minutes = 600,000 milliseconds
    public void checkExpiredPolicies() {
        LocalDate today = LocalDate.now();
        
        logger.debug("Running scheduled check for expired policies on {}", today);
        
        // Check for policies that expired today (expired at midnight)
        List<InsurancePolicy> expiredPolicies = policyRepository.findByEndDate(today);
        
        logger.debug("Found {} policies that expired today", expiredPolicies.size());
        
        for (InsurancePolicy policy : expiredPolicies) {
            // Only log if we haven't already logged this policy
            if (!loggedExpiredPolicies.contains(policy.getId())) {
                logger.info("Policy {} for car {} expired on {}", 
                    policy.getId(), 
                    policy.getCar().getId(), 
                    policy.getEndDate());
                
                // Mark this policy as logged
                loggedExpiredPolicies.add(policy.getId());
            } else {
                logger.debug("Policy {} already logged, skipping", policy.getId());
            }
        }
        
        // Optional: Clean up old entries to prevent memory growth
        // This could be enhanced with a more sophisticated cleanup strategy
        if (loggedExpiredPolicies.size() > 1000) {
            logger.debug("Cleaning up logged expired policies cache");
            loggedExpiredPolicies.clear();
        }
    }
    
    /**
     * Manual method to trigger the expiration check (useful for testing)
     */
    public void triggerExpirationCheck() {
        logger.info("Manually triggering expiration check");
        checkExpiredPolicies();
    }
}
