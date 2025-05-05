package com.nwmsu.vehicle.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nwmsu.vehicle.dto.MaintenanceReportDTO;
import com.nwmsu.vehicle.dto.VehicleReportDTO;
import com.nwmsu.vehicle.repository.FuelingEventRepo;
import com.nwmsu.vehicle.repository.MaintenanceEventRepo;
import com.nwmsu.vehicle.repository.VehicleRepo;

@Service
public class ReportService {

    @Autowired
    private VehicleRepo vehicleRepository;

    @Autowired
    private FuelingEventRepo fuelingEventRepository;

    @Autowired
    private MaintenanceEventRepo maintenanceEventRepository;

    // ✅ Optimized Report 1: Vehicles with Model Year and Current Mileage
    public List<VehicleReportDTO> getAllVehiclesWithMileage() {
        return vehicleRepository.findAll().stream()
                .map(vehicle -> {
                    VehicleReportDTO dto = new VehicleReportDTO();
                    dto.setVehicleNumber(vehicle.getVehicleNumber());
                    dto.setModelYear(vehicle.getModelYear());

                    // ✅ Fetch latest mileage safely
                    Double latestMileage = fuelingEventRepository.getLatestMileageByVehicle(vehicle.getId());
                    dto.setCurrentMileage(latestMileage != null ? latestMileage : vehicle.getStartingMileage());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<VehicleReportDTO> filterVehiclesWithMileage(String vehicleNumber, Integer minMileage, Integer maxMileage) {
        return getAllVehiclesWithMileage().stream()
                .filter(report -> vehicleNumber == null || report.getVehicleNumber().toLowerCase().contains(vehicleNumber.toLowerCase()))
                .filter(report -> minMileage == null || report.getCurrentMileage() >= minMileage)
                .filter(report -> maxMileage == null || report.getCurrentMileage() <= maxMileage)
                .collect(Collectors.toList());
    }
    
    /* public List<VehicleReportDTO> filterVehiclesWithMileage(String vehicleNumber, Integer minMileage, Integer maxMileage) {
        return vehicleRepository.findAll().stream()
                .map(vehicle -> {
                    int currentMileage = calculateMileage(vehicle); // You may already have this method
                    return new VehicleReportDTO(vehicle.getVehicleNumber(), vehicle.getModelYear(), currentMileage);
                })
                .filter(dto ->
                        (vehicleNumber == null || dto.getVehicleNumber().toLowerCase().contains(vehicleNumber.toLowerCase())) &&
                        (minMileage == null || dto.getCurrentMileage() >= minMileage) &&
                        (maxMileage == null || dto.getCurrentMileage() <= maxMileage)
                )
                .collect(Collectors.toList());
    } */



    // ✅ Optimized Report 3: Maintenance Events
    public List<MaintenanceReportDTO> getAllMaintenanceEvents() {
        return maintenanceEventRepository.findAll().stream()
                .map(event -> {
                    MaintenanceReportDTO dto = new MaintenanceReportDTO();
                    dto.setVehicleNumber(event.getVehicle().getVehicleNumber());
                    dto.setDate(event.getDate()); // ✅ Ensure ISO Date format (yyyy-MM-dd)
                    dto.setMaintenanceCost(event.getMaintenanceCost());
                    dto.setMaintenanceDescription(event.getMaintenanceDescription());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<MaintenanceReportDTO> filterMaintenanceEvents(
            String vehicleNumber,
            LocalDate startDate,
            LocalDate endDate,
            Double minCost,
            Double maxCost
    ) {
        return getAllMaintenanceEvents().stream()
                .filter(report -> vehicleNumber == null || report.getVehicleNumber().toLowerCase().contains(vehicleNumber.toLowerCase()))
                .filter(report -> startDate == null || !report.getDate().isBefore(startDate))
                .filter(report -> endDate == null || !report.getDate().isAfter(endDate))
                .filter(report -> minCost == null || report.getMaintenanceCost() >= minCost)
                .filter(report -> maxCost == null || report.getMaintenanceCost() <= maxCost)
                .collect(Collectors.toList());
    }
}
