package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.service.CarService;
import com.example.carins.service.CarHistoryService;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.CarHistoryEventDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {

    private final CarService service;
    private final CarHistoryService historyService;

    public CarController(CarService service, CarHistoryService historyService) {
        this.service = service;
        this.historyService = historyService;
    }

    @GetMapping("/cars")
    public List<CarDto> getCars() {
        return service.listCars().stream().map(this::toDto).toList();
    }

    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> isInsuranceValid(@PathVariable Long carId, @RequestParam String date) {
        // Validate date format
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD format.");
        }
        
        // Validate date range - reject impossible dates
        validateDateRange(parsedDate);
        
        boolean valid = service.isInsuranceValid(carId, parsedDate);
        return ResponseEntity.ok(new InsuranceValidityResponse(carId, parsedDate.toString(), valid));
    }
    
    private void validateDateRange(LocalDate date) {
        // Define reasonable date boundaries
        LocalDate minDate = LocalDate.of(1900, 1, 1);
        LocalDate maxDate = LocalDate.of(2100, 12, 31);
        
        if (date.isBefore(minDate)) {
            throw new IllegalArgumentException("Date cannot be before " + minDate);
        }
        if (date.isAfter(maxDate)) {
            throw new IllegalArgumentException("Date cannot be after " + maxDate);
        }
    }

    @GetMapping("/cars/{carId}/history")
    public ResponseEntity<List<CarHistoryEventDto>> getCarHistory(@PathVariable Long carId) {
        List<CarHistoryEventDto> history = historyService.getCarHistory(carId);
        return ResponseEntity.ok(history);
    }

    private CarDto toDto(Car c) {
        var o = c.getOwner();
        return new CarDto(c.getId(), c.getVin(), c.getMake(), c.getModel(), c.getYearOfManufacture(),
                o != null ? o.getId() : null,
                o != null ? o.getName() : null,
                o != null ? o.getEmail() : null);
    }

    public record InsuranceValidityResponse(Long carId, String date, boolean valid) {}
}
