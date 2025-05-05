package com.nwmsu.vehicle.service;

import java.time.Month;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nwmsu.vehicle.dto.GraphReportDTO;
import com.nwmsu.vehicle.repository.FuelingEventRepo;
import com.nwmsu.vehicle.repository.MaintenanceEventRepo;
import com.nwmsu.vehicle.repository.VehicleRepo;

@Service
public class GraphReportService {

    @Autowired
    private FuelingEventRepo fuelingEventRepository;

    @Autowired
    private MaintenanceEventRepo maintenanceEventRepository;

    @Autowired
    private VehicleRepo vehicleRepository;

    public List<GraphReportDTO> getGraphReport(int year) {
        return List.of(Month.values()).stream()
                .map(month -> {
                    GraphReportDTO report = new GraphReportDTO();
                    report.setMonth(month.name());
                    report.setFuelingExpense(
                        Optional.ofNullable(fuelingEventRepository.getTotalFuelingExpense(year, month.getValue()))
                                .orElse(0.0) // Default to 0 if null
                    );
                    report.setMaintenanceExpense(
                        Optional.ofNullable(maintenanceEventRepository.getTotalMaintenanceExpense(year, month.getValue()))
                                .orElse(0.0) // Default to 0 if null
                    );
                    report.setMilesDriven(
                        Optional.ofNullable(vehicleRepository.getTotalMilesDriven(year, month.getValue()))
                                .orElse(0) // Default to 0 if null
                    );
                    return report;
                })
                .collect(Collectors.toList());
    }
    
    public List<GraphReportDTO> getGraphReportByDate(int year, int month) {
        List<GraphReportDTO> reports = new ArrayList<>();

        // ✅ Fetch Fuel Expense by Date
        List<Object[]> fuelExpenseData = fuelingEventRepository.getFuelExpenseGroupedByDate(year, month);
        List<Object[]> maintenanceExpenseData = maintenanceEventRepository.getMaintenanceExpenseGroupedByDate(year, month);
        List<Object[]> milesDrivenData = vehicleRepository.getMilesDrivenGroupedByDate(year, month);

        // ✅ Convert Raw Data into DTOs
        Map<String, GraphReportDTO> reportMap = new LinkedHashMap<>();

        for (Object[] data : fuelExpenseData) {
            String date = (String) data[0];
            double expense = ((Number) data[1]).doubleValue();
            reportMap.putIfAbsent(date, new GraphReportDTO(date));
            reportMap.get(date).setFuelingExpense(expense);
        }

        for (Object[] data : maintenanceExpenseData) {
            String date = (String) data[0];
            double expense = ((Number) data[1]).doubleValue();
            reportMap.putIfAbsent(date, new GraphReportDTO(date));
            reportMap.get(date).setMaintenanceExpense(expense);
        }

        for (Object[] data : milesDrivenData) {
            String date = (String) data[0];
            int miles = ((Number) data[1]).intValue();
            reportMap.putIfAbsent(date, new GraphReportDTO(date));
            reportMap.get(date).setMilesDriven(miles);
        }

        reports.addAll(reportMap.values());

        return reports;
    }
}

