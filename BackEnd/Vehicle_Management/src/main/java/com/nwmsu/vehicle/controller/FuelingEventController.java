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

import com.nwmsu.vehicle.dto.FuelingEventDTO;
import com.nwmsu.vehicle.service.FuelingEventService;

@RestController
@RequestMapping("api/admin/fueling")
public class FuelingEventController {

    @Autowired
    private FuelingEventService fuelingEventService;

    @PostMapping("/add/{userId}")
    public Map<String, String> addFuelingEvent(
            @PathVariable("userId") String userId, 
            @RequestBody FuelingEventDTO fuelingEventDTO) {
        try {
            fuelingEventService.addFuelingEvent(fuelingEventDTO, userId);
            return Map.of("message", "Fueling event added successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }

    // Get Fueling Events (User & Admin)
    @GetMapping("/list/{userId}/{isAdmin}")
    public List<FuelingEventDTO> getFuelingEvents(
            @PathVariable("userId") String userId, 
            @PathVariable("isAdmin") boolean isAdmin) {
        return fuelingEventService.getFuelingEvents(userId, isAdmin);
    }

    // Update Fueling Event (User & Admin)
    @PutMapping("/update/{eventId}/{userId}/{isAdmin}")
    public Map<String, String> updateFuelingEvent(
            @PathVariable("eventId") Long eventId,
            @RequestBody FuelingEventDTO fuelingEventDTO,
            @PathVariable("userId") String userId,
            @PathVariable("isAdmin") boolean isAdmin) {
        try {
            fuelingEventService.updateFuelingEvent(eventId, fuelingEventDTO, userId, isAdmin);
            return Map.of("message", "Fueling event updated successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{eventId}/{userId}/{isAdmin}")
    public Map<String, String> deleteFuelingEvent(
            @PathVariable("eventId") Long eventId,
            @PathVariable("userId") String userId,
            @PathVariable("isAdmin") boolean isAdmin) {
        try {
            fuelingEventService.deleteFuelingEvent(eventId, userId, isAdmin);
            return Map.of("message", "Fueling event deleted successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }

}

