package com.nwmsu.vehicle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleReportDTO {
	
    private String vehicleNumber;
    
    private int modelYear;
    
    private double currentMileage;
    
}

