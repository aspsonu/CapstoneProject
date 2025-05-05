package com.nwmsu.vehicle.controller;

import com.nwmsu.vehicle.dto.GovernmentReportDTO;
import com.nwmsu.vehicle.report.GovernmentReportGenerator;
import com.nwmsu.vehicle.report.GovernmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/reports/government")
public class GovernmentReportController {

    @Autowired
    private GovernmentReportService governmentReportService;

    @Autowired
    private GovernmentReportGenerator excelExporter;

    @GetMapping
    public List<GovernmentReportDTO> getGovernmentReport(
            @RequestParam(name = "fiscalYear", required = false) Integer fiscalYear,
            @RequestParam(name = "month", required = false) String month) {
        return governmentReportService.generateReport(fiscalYear, month);
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadGovernmentReportExcel(
            @RequestParam(name = "fiscalYear", required = false) Integer fiscalYear,
            @RequestParam(name = "month", required = false) String month) {

        InputStream excel = excelExporter.export(governmentReportService.generateReport(fiscalYear, month));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Government_Report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excel));
    }
}
