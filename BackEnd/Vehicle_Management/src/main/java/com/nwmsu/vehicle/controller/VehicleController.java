package com.nwmsu.vehicle.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nwmsu.vehicle.dto.VehicleDTO;
import com.nwmsu.vehicle.service.VehicleService;

@RestController
@RequestMapping("/api/admin/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping("/add")
    public Map<String, String> addVehicle(@RequestBody VehicleDTO vehicleDTO) {
        try {
            vehicleService.addVehicle(vehicleDTO);
            return Map.of("message", "Vehicle added successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }
    
    @GetMapping("/list")
    public List<VehicleDTO> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }
    
    @PutMapping("/update/{vehicleId}")
    public Map<String, String> updateVehicle(@PathVariable("vehicleId") Long vehicleId, @RequestBody VehicleDTO vehicleDTO) {
        try {
            vehicleService.updateVehicle(vehicleId, vehicleDTO);
            return Map.of("message", "Vehicle updated successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{vehicleId}")
    public Map<String, String> deleteVehicle(@PathVariable("vehicleId") Long vehicleId) {
        try {
            vehicleService.deleteVehicle(vehicleId);
            return Map.of("message", "Vehicle deleted successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }
    
    @PutMapping("/reactivate/{vehicleId}")
    public Map<String, String> reactivateVehicle(@PathVariable("vehicleId") Long vehicleId) {
        try {
            vehicleService.reactivateVehicle(vehicleId);
            return Map.of("message", "Vehicle reactivated successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }

}

