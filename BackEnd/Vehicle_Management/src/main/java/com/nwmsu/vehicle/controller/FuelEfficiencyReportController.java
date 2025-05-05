package com.nwmsu.vehicle.controller;

import com.nwmsu.vehicle.dto.FuelEfficiencyDTO;
import com.nwmsu.vehicle.report.FuelEfficiencyReportGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class FuelEfficiencyReportController {

    @Autowired
    private FuelEfficiencyReportGenerator reportGenerator;

    @GetMapping("/fuel-efficiency")
    public ResponseEntity<List<FuelEfficiencyDTO>> getFilteredEfficiencies(
            @RequestParam(name = "vehicleNumber", required = false) String vehicleNumber,
            @RequestParam(name = "minMpg", required = false) Double minMpg,
            @RequestParam(name = "maxMpg", required = false) Double maxMpg
    ) {
        return ResponseEntity.ok(reportGenerator.getFilteredEfficiencies(vehicleNumber, minMpg, maxMpg));
    }

    @GetMapping("/fuel-efficiency/download")
    public ResponseEntity<InputStreamResource> downloadFilteredFuelEfficiencyReport(
            @RequestParam(name = "vehicleNumber", required = false) String vehicleNumber,
            @RequestParam(name = "minMpg", required = false) Double minMpg,
            @RequestParam(name = "maxMpg", required = false) Double maxMpg
    ) {
        InputStream file = reportGenerator.generateFuelEfficiencyExcelReport(vehicleNumber, minMpg, maxMpg);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Fuel_Efficiency_Report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(file));
    }

}

