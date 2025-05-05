package com.nwmsu.vehicle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nwmsu.vehicle.dto.VehicleDTO;
import com.nwmsu.vehicle.entity.User;
import com.nwmsu.vehicle.entity.Vehicle;
import com.nwmsu.vehicle.repository.VehicleRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepo vehicleRepository;

    public void addVehicle(VehicleDTO vehicleDTO) {
        Optional<Vehicle> existingVehicle = vehicleRepository.findByVehicleIdentificationNumber(vehicleDTO.getVehicleIdentificationNumber());
        
        if (existingVehicle.isPresent()) {
            throw new RuntimeException("Vehicle with this VIN already exists!");
        }

        boolean exemptType = vehicleDTO.isLawEnforcement() || vehicleDTO.getVehicleWeight().equals("> 8,500 Pounds");

        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(vehicleDTO.getVehicleNumber())
                .vehicleIdentificationNumber(vehicleDTO.getVehicleIdentificationNumber())
                .modelYear(vehicleDTO.getModelYear())
                .make(vehicleDTO.getMake())
                .model(vehicleDTO.getModel())
                .purchaseDate(vehicleDTO.getPurchaseDate())
                .startingMileage(vehicleDTO.getStartingMileage())
                .vehicleWeight(vehicleDTO.getVehicleWeight())
                .vehicleType(vehicleDTO.getVehicleType())
                .vehicleDescription(vehicleDTO.getVehicleDescription())
                .lawEnforcement(vehicleDTO.isLawEnforcement())
                .exemptType(exemptType)
                .build();

        vehicleRepository.save(vehicle);
    }
    
    private int parseWeight(String weight) {
        try {
            // Extract only numbers from the weight string
            String numericWeight = weight.replaceAll("[^0-9]", ""); 
            
            // Convert to integer
            return Integer.parseInt(numericWeight);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid vehicle weight format: " + weight);
        }
    }

    
    public List<VehicleDTO> getAllVehicles() {
        //List<Vehicle> vehicles = vehicleRepository.findAll();
        List<Vehicle> vehicles = vehicleRepository.findAll()
                .stream()
                .sorted((a, b) -> Long.compare(b.getId(), a.getId())) // 👈 Sort by latest ID
                .collect(Collectors.toList());
        return vehicles.stream()
                .map(vehicle -> {
                    VehicleDTO dto = new VehicleDTO();
                    dto.setVehicleId(vehicle.getId());
                    dto.setVehicleNumber(vehicle.getVehicleNumber());
                    dto.setVehicleIdentificationNumber(vehicle.getVehicleIdentificationNumber());
                    dto.setModelYear(vehicle.getModelYear());
                    dto.setMake(vehicle.getMake());
                    dto.setModel(vehicle.getModel());
                    dto.setPurchaseDate(vehicle.getPurchaseDate());
                    dto.setStartingMileage(vehicle.getStartingMileage());
                    dto.setVehicleWeight(vehicle.getVehicleWeight());
                    dto.setVehicleType(vehicle.getVehicleType());
                    dto.setVehicleDescription(vehicle.getVehicleDescription());
                    dto.setLawEnforcement(vehicle.isLawEnforcement());
                    dto.setExemptType(vehicle.isExemptType());
                    dto.setDeleted(vehicle.isDeleted());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    public void updateVehicle(Long vehicleId, VehicleDTO vehicleDTO) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        
        if (vehicleOpt.isEmpty()) {
            throw new RuntimeException("Vehicle not found!");
        }

        Vehicle vehicle = vehicleOpt.get();

        // Update allowed fields only
        vehicle.setVehicleNumber(vehicleDTO.getVehicleNumber());
        vehicle.setModelYear(vehicleDTO.getModelYear());
        vehicle.setMake(vehicleDTO.getMake());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setPurchaseDate(vehicleDTO.getPurchaseDate());
        vehicle.setStartingMileage(vehicleDTO.getStartingMileage());
        vehicle.setVehicleWeight(vehicleDTO.getVehicleWeight());
        vehicle.setVehicleType(vehicleDTO.getVehicleType());
        vehicle.setVehicleDescription(vehicleDTO.getVehicleDescription());
        vehicle.setLawEnforcement(vehicleDTO.isLawEnforcement());

        // Determine exempt type
        //boolean exemptType = vehicleDTO.isLawEnforcement() || vehicleDTO.getVehicleWeight().equals("> 8,500 Pounds") || parseWeight(vehicleDTO.getVehicleWeight()) > 8500;
        boolean exemptType = vehicleDTO.isLawEnforcement() || vehicleDTO.getVehicleWeight().equals("> 8,500 Pounds");
        vehicle.setExemptType(exemptType);

        vehicleRepository.save(vehicle);
    }
    
    public void deleteVehicle(Long vehicleId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);

        if (vehicleOpt.isEmpty()) {
            throw new RuntimeException("Vehicle not found!");
        }

        //vehicleRepository.delete(vehicleOpt.get());
        Vehicle vehicle = vehicleOpt.get();
        vehicle.setDeleted(true); // ✅ Soft delete
        vehicleRepository.save(vehicle);

    }
    
    public void reactivateVehicle(Long vehicleId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (vehicleOpt.isEmpty()) throw new RuntimeException("Vehicle not found!");

        Vehicle vehicle = vehicleOpt.get();
        if (!vehicle.isDeleted()) throw new RuntimeException("Vehicle is already active.");

        vehicle.setDeleted(false);
        vehicleRepository.save(vehicle);
    }

}

