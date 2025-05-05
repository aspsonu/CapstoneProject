package com.nwmsu.vehicle.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nwmsu.vehicle.dto.MaintenanceEventDTO;
import com.nwmsu.vehicle.entity.MaintenanceEvent;
import com.nwmsu.vehicle.entity.User;
import com.nwmsu.vehicle.entity.Vehicle;
import com.nwmsu.vehicle.repository.MaintenanceEventRepo;
import com.nwmsu.vehicle.repository.UserRepo;
import com.nwmsu.vehicle.repository.VehicleRepo;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
public class MaintenanceEventService {

    @Autowired
    private MaintenanceEventRepo maintenanceEventRepository;

    @Autowired
    private VehicleRepo vehicleRepository;
    
    @Autowired
    private UserRepo userRepository;

    public void addMaintenanceEvent(MaintenanceEventDTO maintenanceEventDTO, String userId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findByVehicleNumber(maintenanceEventDTO.getVehicleNumber());
        Optional<User> userOpt = userRepository.findByUserId(userId);

        if (vehicleOpt.isEmpty()) {
            throw new RuntimeException("Vehicle with number " + maintenanceEventDTO.getVehicleNumber() + " does not exist.");
        }

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        Vehicle vehicle = vehicleOpt.get();

        // 🔹 Parse dates
        LocalDate purchaseDate;
        LocalDate maintenanceDate = maintenanceEventDTO.getDate();
        LocalDate today = LocalDate.now();

        try {
            purchaseDate = LocalDate.parse(vehicle.getPurchaseDate()); // assuming it’s stored in yyyy-MM-dd format
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid purchase date format for vehicle.");
        }

        // 🔴 Validation 1: Maintenance date must not be before purchase date
        if (maintenanceDate.isBefore(purchaseDate)) {
            throw new RuntimeException("Maintenance date cannot be before vehicle purchase date: " + purchaseDate);
        }

        // 🔴 Validation 2: Maintenance date must not be after today
        if (maintenanceDate.isAfter(today)) {
            throw new RuntimeException("Maintenance date cannot be a future date: " + maintenanceDate);
        }

        // 🔹 Save if valid
        MaintenanceEvent event = MaintenanceEvent.builder()
                .vehicle(vehicle)
                .date(maintenanceDate)
                .maintenanceCost(maintenanceEventDTO.getMaintenanceCost())
                .maintenanceDescription(maintenanceEventDTO.getMaintenanceDescription())
                .createdBy(userOpt.get())
                .build();

        maintenanceEventRepository.save(event);
    }

    // Fetch Maintenance Events for User & Admin
    public List<MaintenanceEventDTO> getMaintenanceEvents(String userId, boolean isAdmin) {
        Optional<User> userOpt = userRepository.findByUserId(userId);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with userId: " + userId);
        }

        User user = userOpt.get();
        List<MaintenanceEvent> events = isAdmin
                ? maintenanceEventRepository.findAllByOrderByDateDesc() // Admin gets all events
                : maintenanceEventRepository.findByUserIdOrderByDateDesc(userId);
                	//maintenanceEventRepository.findByCreatedByOrderByDateDesc(user); // User gets only their own

        return events.stream()
                .map(event -> new MaintenanceEventDTO(
                		event.getId(),
                        event.getVehicle().getVehicleNumber(),
                        event.getDate(),
                        event.getMaintenanceCost(),
                        event.getMaintenanceDescription()))
                .collect(Collectors.toList());
    }

    
    public void updateMaintenanceEvent(Long eventId, MaintenanceEventDTO maintenanceEventDTO, String userId, boolean isAdmin) {
        Optional<MaintenanceEvent> eventOpt = isAdmin
                ? maintenanceEventRepository.findById(eventId) // Admin can update any event
                : Optional.ofNullable(maintenanceEventRepository.findByIdAndCreatedBy(eventId, userId)); // Users can update only their own

        if (eventOpt.isEmpty()) {
            throw new RuntimeException("Maintenance event not found or access denied.");
        }

        MaintenanceEvent event = eventOpt.get();
        event.setDate(maintenanceEventDTO.getDate());
        event.setMaintenanceCost(maintenanceEventDTO.getMaintenanceCost());
        event.setMaintenanceDescription(maintenanceEventDTO.getMaintenanceDescription());

        maintenanceEventRepository.save(event);
    }

    public void deleteMaintenanceEvent(Long eventId, String userId, boolean isAdmin) {
        Optional<MaintenanceEvent> eventOpt = isAdmin
                ? maintenanceEventRepository.findById(eventId) // Admin can delete any event
                : Optional.ofNullable(maintenanceEventRepository.findByIdAndCreatedBy(eventId, userId)); // Users can delete only their own

        if (eventOpt.isEmpty()) {
            throw new RuntimeException("Maintenance event not found or access denied.");
        }

        maintenanceEventRepository.delete(eventOpt.get());
    }

}
