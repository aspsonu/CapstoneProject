package com.nwmsu.vehicle.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraphReportDTO {

	private String month;
    
    private double fuelingExpense;
    
    private double maintenanceExpense;
    
    private int milesDriven;
    
    private String date;

	public GraphReportDTO(String date) {
		super();
		this.date = date;
	}
    
}

