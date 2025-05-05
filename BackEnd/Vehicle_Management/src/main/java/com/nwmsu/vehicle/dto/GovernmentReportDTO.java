package com.nwmsu.vehicle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GovernmentReportDTO {
    private String vehicleType;
    private String vehicleDescription;
    private int lessThan8500;
    private int greaterThan8500;
    private int milesTravelled;
    private double gasOrDieselGallons;
    private double altFuelGallons;
    private double gasOrDieselCost;
    private double altFuelCost;
    private double maintenanceCost;
}
