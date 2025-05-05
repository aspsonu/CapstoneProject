package com.nwmsu.vehicle.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nwmsu.vehicle.dto.GraphReportDTO;
import com.nwmsu.vehicle.service.GraphReportService;

@RestController
@RequestMapping("/api/admin/graph")
public class GraphReportController {

    @Autowired
    private GraphReportService graphReportService;

    @GetMapping("/report/{year}")
    public List<GraphReportDTO> getGraphReport(@PathVariable("year") int year) {
        return graphReportService.getGraphReport(year);
    }
    
    @GetMapping("/daily-report/{year}/{month}")
    public List<GraphReportDTO> getGraphReportByDate(@PathVariable("year") int year, 
                                                     @PathVariable("month") int month) {
        return graphReportService.getGraphReportByDate(year, month);
    }
}

