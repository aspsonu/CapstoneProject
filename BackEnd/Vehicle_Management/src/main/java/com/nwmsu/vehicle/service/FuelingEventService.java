package com.nwmsu.vehicle.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nwmsu.vehicle.dto.FuelingEventDTO;
import com.nwmsu.vehicle.entity.FuelingEvent;
import com.nwmsu.vehicle.entity.User;
import com.nwmsu.vehicle.entity.Vehicle;
import com.nwmsu.vehicle.repository.FuelingEventRepo;
import com.nwmsu.vehicle.repository.UserRepo;
import com.nwmsu.vehicle.repository.VehicleRepo;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
public class FuelingEventService {

    @Autowired
    private FuelingEventRepo fuelingEventRepository;

    @Autowired
    private VehicleRepo vehicleRepository;

    @Autowired
    private UserRepo userRepository;

    public void addFuelingEvent(FuelingEventDTO fuelingEventDTO, String userId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findByVehicleNumber(fuelingEventDTO.getVehicleNumber());
        Optional<User> userOpt = userRepository.findByUserId(userId);

        if (vehicleOpt.isEmpty()) {
            throw new RuntimeException("Vehicle with number " + fuelingEventDTO.getVehicleNumber() + " does not exist.");
        }

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        Vehicle vehicle = vehicleOpt.get();

        // 🔹 Parse and validate dates
        LocalDate fuelingDate = fuelingEventDTO.getDate();
        LocalDate today = LocalDate.now();
        LocalDate purchaseDate;

        try {
            purchaseDate = LocalDate.parse(vehicle.getPurchaseDate()); // Assumes "yyyy-MM-dd" format
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid purchase date format for vehicle.");
        }

        if (fuelingDate.isBefore(purchaseDate)) {
            throw new RuntimeException("Fueling date cannot be before vehicle purchase date: " + purchaseDate);
        }

        if (fuelingDate.isAfter(today)) {
            throw new RuntimeException("Fueling date cannot be a future date: " + fuelingDate);
        }

        FuelingEvent event = FuelingEvent.builder()
                .vehicle(vehicle)
                .date(fuelingDate)
                .currentMileage(fuelingEventDTO.getCurrentMileage())
                .fuelAdded(fuelingEventDTO.getFuelAdded())
                .fuelCost(fuelingEventDTO.getFuelCost())
                .createdBy(userOpt.get()) // Associate with user
                .build();

        fuelingEventRepository.save(event);
    }

    // Get Fueling Events (User & Admin)
    public List<FuelingEventDTO> getFuelingEvents(String userId, boolean isAdmin) {
        Optional<User> userOpt = userRepository.findByUserId(userId);  // 🔹 Find by userId as String

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with userId: " + userId);
        }

        User user = userOpt.get();
        List<FuelingEvent> events = isAdmin
                ? fuelingEventRepository.findAllByOrderByDateDesc() // Admin gets all events
                : fuelingEventRepository.findByCreatedByOrderByDateDesc(user); // User gets only their own

        return events.stream()
                .map(event -> new FuelingEventDTO(
                		event.getId(),
                        event.getVehicle().getVehicleNumber(),
                        event.getDate(),
                        event.getCurrentMileage(),
                        event.getFuelAdded(),
                        event.getFuelCost()))
                .collect(Collectors.toList());
    }

    // Update Fueling Event (Only if User is Owner OR Admin)
    public void updateFuelingEvent(Long eventId, FuelingEventDTO fuelingEventDTO, String userId, boolean isAdmin) {
        Optional<FuelingEvent> eventOpt = isAdmin
                ? fuelingEventRepository.findById(eventId)
                : Optional.ofNullable(fuelingEventRepository.findByIdAndCreatedBy(eventId, userId));

        if (eventOpt.isEmpty()) {
            throw new RuntimeException("Fueling event not found or access denied.");
        }

        FuelingEvent event = eventOpt.get();
        event.setDate(fuelingEventDTO.getDate());
        event.setCurrentMileage(fuelingEventDTO.getCurrentMileage());
        event.setFuelAdded(fuelingEventDTO.getFuelAdded());
        event.setFuelCost(fuelingEventDTO.getFuelCost());

        fuelingEventRepository.save(event);
    }
    
    public void deleteFuelingEvent(Long eventId, String userId, boolean isAdmin) {
        Optional<FuelingEvent> eventOpt = isAdmin
                ? fuelingEventRepository.findById(eventId) // Admin can delete any event
                : Optional.ofNullable(fuelingEventRepository.findByIdAndCreatedBy(eventId, userId)); // Users can delete only their own

        if (eventOpt.isEmpty()) {
            throw new RuntimeException("Fueling event not found or access denied.");
        }

        fuelingEventRepository.delete(eventOpt.get());
    }

}

