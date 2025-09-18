package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.service.CarService;
import com.example.carins.service.CarHistoryService;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.CarHistoryEventDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
        // TODO: validate date format and handle errors consistently
        LocalDate d = LocalDate.parse(date);
        boolean valid = service.isInsuranceValid(carId, d);
        return ResponseEntity.ok(new InsuranceValidityResponse(carId, d.toString(), valid));
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
