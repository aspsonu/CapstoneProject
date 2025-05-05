package com.nwmsu.vehicle.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MaintenanceReportDTO {
	
    private String vehicleNumber;
    
    private LocalDate date;
    
    private double maintenanceCost;
    
    private String maintenanceDescription;
    
}

