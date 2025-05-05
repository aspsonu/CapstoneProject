package com.nwmsu.vehicle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class FuelEfficiencyDTO {
	
    private String vehicleNumber;
    
    //private double milesPerGallon;
    
    private double fuelEfficiency;
    
}

