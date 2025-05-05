package com.nwmsu.vehicle.report;

import com.nwmsu.vehicle.dto.GovernmentReportDTO;
import com.nwmsu.vehicle.entity.FuelingEvent;
import com.nwmsu.vehicle.entity.MaintenanceEvent;
import com.nwmsu.vehicle.entity.Vehicle;
import com.nwmsu.vehicle.repository.FuelingEventRepo;
import com.nwmsu.vehicle.repository.MaintenanceEventRepo;
import com.nwmsu.vehicle.repository.VehicleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GovernmentReportService {

    @Autowired
    private VehicleRepo vehicleRepo;

    @Autowired
    private FuelingEventRepo fuelingEventRepo;

    @Autowired
    private MaintenanceEventRepo maintenanceEventRepo;

    private static final List<String> GAS_DIESEL = Arrays.asList("Gasoline", "Diesel");

    public List<GovernmentReportDTO> generateReport(Integer fiscalYear, String monthFilter) {
        List<Vehicle> vehicles = vehicleRepo.findAll();
        List<GovernmentReportDTO> reportList = new ArrayList<>();

        List<String> vehicleTypes = Arrays.asList("Gasoline", "Hybrid", "Diesel", "E85", "CNG", "Propane", "Electric");
        List<String> descriptions = Arrays.asList("Cars and Station Wagons", "LDTs, Vans, SUVs", "Exempt Vehicles");

        int fyStartYear = (fiscalYear != null) ? fiscalYear : LocalDate.now().getYear();
        LocalDate startDate = LocalDate.of(fyStartYear, Month.JULY, 1);
        LocalDate endDate = LocalDate.of(fyStartYear + 1, Month.JUNE, 30);

        if (monthFilter != null) {
            try {
                Month month = Month.valueOf(monthFilter.toUpperCase());
                startDate = LocalDate.of(fyStartYear + (month.getValue() < 7 ? 1 : 0), month, 1);
                endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            } catch (IllegalArgumentException ignored) {}
        }

        for (String type : vehicleTypes) {
            for (String desc : descriptions) {
            	/*if ("Electric".equalsIgnoreCase(type) && !"LDTs, Vans, SUVs".equalsIgnoreCase(desc)) {
                    continue;
                }*/
                List<Vehicle> filteredVehicles = vehicles.stream()
                        .filter(v -> type.equalsIgnoreCase(v.getVehicleType()))
                        .filter(v -> {
                            if ("Exempt Vehicles".equals(desc)) return Boolean.TRUE.equals(v.isExemptType());
                            return desc.equalsIgnoreCase(getDescriptionCategory(v));
                        })
                        .collect(Collectors.toList());

                int countLess = (int) filteredVehicles.stream()
                        .filter(v -> "<= 8,500 Pounds".equals(v.getVehicleWeight()))
                        .count();

                int countMore = (int) filteredVehicles.stream()
                        .filter(v -> "> 8,500 Pounds".equals(v.getVehicleWeight()))
                        .count();

                int miles = 0;
                double gasOrDieselGallons = 0, altFuelGallons = 0;
                double gasOrDieselCost = 0, altFuelCost = 0, maintenanceCost = 0;

                for (Vehicle v : filteredVehicles) {
                    List<FuelingEvent> fueling = fuelingEventRepo.findByVehicleAndDateBetween(v, startDate, endDate);
                    List<MaintenanceEvent> maintenance = maintenanceEventRepo.findByVehicleAndDateBetween(v, startDate, endDate);

                    for (FuelingEvent f : fueling) {
                        miles += f.getCurrentMileage();
                        if (GAS_DIESEL.contains(v.getVehicleType())) {
                            gasOrDieselGallons += f.getFuelAdded();
                            gasOrDieselCost += f.getFuelCost();
                        } else {
                            altFuelGallons += f.getFuelAdded();
                            altFuelCost += f.getFuelCost();
                        }
                    }

                    maintenanceCost += maintenance.stream()
                            .mapToDouble(MaintenanceEvent::getMaintenanceCost)
                            .sum();
                }

                GovernmentReportDTO dto = new GovernmentReportDTO();
                dto.setVehicleType(type);
                dto.setVehicleDescription(desc);
                dto.setLessThan8500(countLess);
                dto.setGreaterThan8500(countMore);
                dto.setMilesTravelled(miles);
                dto.setGasOrDieselGallons(gasOrDieselGallons);
                dto.setAltFuelGallons(altFuelGallons);
                dto.setGasOrDieselCost(gasOrDieselCost);
                dto.setAltFuelCost(altFuelCost);
                dto.setMaintenanceCost(maintenanceCost);

                reportList.add(dto);
            }
        }

        return reportList;
    }

    private String getDescriptionCategory(Vehicle v) {
        if (Boolean.TRUE.equals(v.isExemptType())) return "Exempt Vehicles";
        String desc = v.getVehicleDescription();
        if (desc != null && desc.toLowerCase().contains("car")) return "Cars and Station Wagons";
        return "LDTs, Vans, SUVs";
    }
}
