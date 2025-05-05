package com.nwmsu.vehicle.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuelingEventDTO {
	
	private Long id;
	
    private String vehicleNumber;
    
    private LocalDate date;
    
    private int currentMileage;
    
    private double fuelAdded;
    
    private double fuelCost;
    
}
