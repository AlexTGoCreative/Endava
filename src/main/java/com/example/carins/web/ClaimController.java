package com.example.carins.web;

import com.example.carins.service.ClaimService;
import com.example.carins.web.dto.ClaimCreateDto;
import com.example.carins.web.dto.ClaimResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/cars/{carId}/claims")
    public ResponseEntity<ClaimResponseDto> createClaim(
            @PathVariable Long carId, 
            @Valid @RequestBody ClaimCreateDto claimDto) {
        
        ClaimResponseDto createdClaim = claimService.createClaim(carId, claimDto);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdClaim.id())
            .toUri();
            
        return ResponseEntity.created(location).body(createdClaim);
    }

    @GetMapping("/cars/{carId}/claims")
    public ResponseEntity<List<ClaimResponseDto>> getClaimsByCarId(@PathVariable Long carId) {
        List<ClaimResponseDto> claims = claimService.getClaimsByCarId(carId);
        return ResponseEntity.ok(claims);
    }
}
