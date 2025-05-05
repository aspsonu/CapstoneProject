package com.nwmsu.vehicle.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nwmsu.vehicle.service.ExportService;

@RestController
@RequestMapping("/api/admin/export")
public class ExportController {

    @Autowired
    private ExportService exportService;
    
    @GetMapping("/maintenance/pdf")
    public ResponseEntity<InputStreamResource> downloadFilteredMaintenanceReport(
            @RequestParam(name = "vehicleNumber", required = false) String vehicleNumber,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "minCost", required = false) Double minCost,
            @RequestParam(name = "maxCost", required = false) Double maxCost
    ) {
        InputStreamResource pdf = new InputStreamResource(
                exportService.exportFilteredMaintenanceEvents(vehicleNumber, startDate, endDate, minCost, maxCost)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Filtered_Maintenance_Report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    
 // ReportController.java
    @GetMapping("/vehicles-mileage/download")
    public ResponseEntity<InputStreamResource> downloadFilteredVehicleMileageReport(
            @RequestParam(name = "vehicleNumber" ,required = false) String vehicleNumber,
            @RequestParam(name = "minMileage" ,required = false) Integer minMileage,
            @RequestParam(name = "maxMileage" ,required = false) Integer maxMileage) {

        InputStreamResource file = new InputStreamResource(
            exportService.exportFilteredVehicleMileage(vehicleNumber, minMileage, maxMileage)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Filtered_Vehicle_Mileage_Report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }



}
