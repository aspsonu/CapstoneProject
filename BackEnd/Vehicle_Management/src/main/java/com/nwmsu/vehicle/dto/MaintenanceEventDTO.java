package com.nwmsu.vehicle.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceEventDTO {
	
	private Long id;
	
    private String vehicleNumber;
    
    private LocalDate date;
    
    private double maintenanceCost;
    
    private String maintenanceDescription;
    
}


