package com.nwmsu.vehicle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleDTO {
	
	private long vehicleId;
	
    private String vehicleNumber;
    
    private String vehicleIdentificationNumber;
    
    private int modelYear;
    
    private String make;
    
    private String model;
    
    private String purchaseDate;
    
    private int startingMileage;
    
    private String vehicleWeight;
    
    private String vehicleType;
    
    private String vehicleDescription;
    
    private boolean lawEnforcement;
    
    private boolean exemptType;
    
    private boolean deleted;

    
}

