package com.energymix.backend.controller;

import com.energymix.backend.model.DailyEnergyMix;
import com.energymix.backend.model.OptimalWindow;
import com.energymix.backend.service.EnergyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class EnergyController {

    private final EnergyService energyService;

    public EnergyController(EnergyService energyService) {
        this.energyService = energyService;
    }

    @GetMapping("/energy-mix")
    public ResponseEntity<List<DailyEnergyMix>> getEnergyMix() {
        return ResponseEntity.ok(energyService.getEnergyMixForThreeDays());
    }

    @GetMapping("/optimal-window")
    public ResponseEntity<OptimalWindow> getOptimalWindow(@RequestParam int hours) {
        if (hours < 1 || hours > 6) {
            throw new IllegalArgumentException("Hours must be between 1 and 6");
        }
        return ResponseEntity.ok(energyService.findOptimalChargingWindow(hours));
    }
}