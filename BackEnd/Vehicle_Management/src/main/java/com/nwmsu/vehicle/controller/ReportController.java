package com.nwmsu.vehicle.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nwmsu.vehicle.dto.MaintenanceReportDTO;
import com.nwmsu.vehicle.dto.VehicleReportDTO;
import com.nwmsu.vehicle.service.ReportService;

@RestController
@RequestMapping("/api/admin/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/vehicles-mileage")
    public List<VehicleReportDTO> getAllVehiclesWithMileage() {
        return reportService.getAllVehiclesWithMileage();
    }
    
    @GetMapping("/vehicles-mileage/filter")
    public List<VehicleReportDTO> filterVehiclesWithMileage(
            @RequestParam(name = "vehicleNumber", required = false) String vehicleNumber,
            @RequestParam(name = "minMileage" ,required = false) Integer minMileage,
            @RequestParam(name = "maxMileage" ,required = false) Integer maxMileage
    ) {
        return reportService.filterVehiclesWithMileage(vehicleNumber, minMileage, maxMileage);
    }

    @GetMapping("/maintenance-events")
    public List<MaintenanceReportDTO> getAllMaintenanceEvents() {
        return reportService.getAllMaintenanceEvents();
    }
    
    @GetMapping("/maintenance-events/filter")
    public List<MaintenanceReportDTO> filterMaintenanceEvents(
        @RequestParam(name = "vehicleNumber", required = false) String vehicleNumber,
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(name = "minCost", required = false) Double minCost,
        @RequestParam(name = "maxCost", required = false) Double maxCost
    ) {
        return reportService.filterMaintenanceEvents(vehicleNumber, startDate, endDate, minCost, maxCost);
    }

    
}

