package com.nwmsu.vehicle.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nwmsu.vehicle.dto.MaintenanceEventDTO;
import com.nwmsu.vehicle.service.MaintenanceEventService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("api/admin/maintenance")
public class MaintenanceEventController {

    @Autowired
    private MaintenanceEventService maintenanceEventService;

 // Add Maintenance Event (User & Admin)
    @PostMapping("/add/{userId}")
    public Map<String, String> addMaintenanceEvent(
            @PathVariable("userId") String userId,
            @RequestBody MaintenanceEventDTO maintenanceEventDTO) {
        try {
            maintenanceEventService.addMaintenanceEvent(maintenanceEventDTO, userId);
            return Map.of("message", "Maintenance event added successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }

    // Get Maintenance Events (User & Admin)
    @GetMapping("/list/{userId}/{isAdmin}")
    public List<MaintenanceEventDTO> getMaintenanceEvents(
            @PathVariable("userId") String userId,
            @PathVariable("isAdmin") boolean isAdmin) {
        return maintenanceEventService.getMaintenanceEvents(userId, isAdmin);
    }
    
 // Update Maintenance Event (User & Admin)
    @PutMapping("/update/{eventId}/{userId}/{isAdmin}")
    public Map<String, String> updateMaintenanceEvent(
            @PathVariable("eventId") Long eventId,
            @RequestBody MaintenanceEventDTO maintenanceEventDTO,
            @PathVariable("userId") String userId,
            @PathVariable("isAdmin") boolean isAdmin) {
        try {
            maintenanceEventService.updateMaintenanceEvent(eventId, maintenanceEventDTO, userId, isAdmin);
            return Map.of("message", "Maintenance event updated successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{eventId}/{userId}/{isAdmin}")
    public Map<String, String> deleteMaintenanceEvent(
            @PathVariable("eventId") Long eventId,
            @PathVariable("userId") String userId,
            @PathVariable("isAdmin") boolean isAdmin) {
        try {
            maintenanceEventService.deleteMaintenanceEvent(eventId, userId, isAdmin);
            return Map.of("message", "Maintenance event deleted successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }

}
