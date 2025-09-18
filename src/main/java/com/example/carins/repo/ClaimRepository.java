package com.example.carins.repo;

import com.example.carins.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    
    @Query("SELECT c FROM Claim c WHERE c.car.id = :carId ORDER BY c.claimDate DESC, c.createdAt DESC")
    List<Claim> findByCarIdOrderByClaimDateDesc(@Param("carId") Long carId);
    
    @Query("SELECT c FROM Claim c WHERE c.car.id = :carId ORDER BY c.claimDate ASC, c.createdAt ASC")
    List<Claim> findByCarIdOrderByClaimDateAsc(@Param("carId") Long carId);
}
