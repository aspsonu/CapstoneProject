package com.nwmsu.vehicle.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nwmsu.vehicle.dto.FuelEfficiencyDTO;
import com.nwmsu.vehicle.entity.FuelingEvent;
import com.nwmsu.vehicle.entity.Vehicle;
import com.nwmsu.vehicle.repository.FuelingEventRepo;
import com.nwmsu.vehicle.repository.VehicleRepo;

@Service
public class FuelEfficiencyReportGenerator {

    @Autowired
    private VehicleRepo vehicleRepo;

    @Autowired
    private FuelingEventRepo fuelingEventRepo;

    public List<FuelEfficiencyDTO> getFilteredEfficiencies(String vehicleNumber, Double min, Double max) {
        return calculateAllVehicleFuelEfficiencies().stream()
            .filter(dto ->
                (vehicleNumber == null || dto.getVehicleNumber().toLowerCase().contains(vehicleNumber.toLowerCase())) &&
                (min == null || dto.getFuelEfficiency() >= min) &&
                (max == null || dto.getFuelEfficiency() <= max)
            )
            .collect(Collectors.toList());
    }

    public InputStream generateFuelEfficiencyExcelReport(String vehicleNumber, Double minMpg, Double maxMpg) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Fuel Efficiency");

            // 🔹 Bold font
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            // 🔹 Title & Header Style
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(boldFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setBorderTop(BorderStyle.THIN);
            titleStyle.setBorderBottom(BorderStyle.THIN);
            titleStyle.setBorderLeft(BorderStyle.THIN);
            titleStyle.setBorderRight(BorderStyle.THIN);

            // 🔹 Create Title Row 1 → A1:B1
            Row row1 = sheet.createRow(0);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue("Northwest Missouri State University");
            cell1.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

            // 🔹 Create Title Row 2 → A2:B2
            Row row2 = sheet.createRow(1);
            Cell cell2 = row2.createCell(0);
            cell2.setCellValue("Fuel Efficiency Report");
            cell2.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));

            // 🔹 Create Header Row → A3, B3
            Row header = sheet.createRow(2);
            String[] headers = {"Vehicle Number", "Fuel Efficiency (MPG)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(titleStyle);
            }

            // 🔹 Fill Data → A4 onward
            List<FuelEfficiencyDTO> filtered = getFilteredEfficiencies(vehicleNumber, minMpg, maxMpg);
            int rowIdx = 3;
            for (FuelEfficiencyDTO dto : filtered) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getVehicleNumber());
                row.createCell(1).setCellValue(Math.round(dto.getFuelEfficiency() * 100.0) / 100.0);
            }

            // 🔹 Auto-size columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }


    public List<FuelEfficiencyDTO> calculateAllVehicleFuelEfficiencies() {
        List<Vehicle> vehicles = vehicleRepo.findAll();
        List<FuelEfficiencyDTO> result = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            List<FuelingEvent> fuelings = fuelingEventRepo.findByVehicle(vehicle)
                .stream()
                .sorted(Comparator.comparing(FuelingEvent::getDate))
                .collect(Collectors.toList());

            if (fuelings.isEmpty()) continue;

           // FuelingEvent last = fuelings.get(fuelings.size() - 1);
            FuelingEvent last = fuelings.stream()
            	    .max(Comparator.comparingInt(FuelingEvent::getCurrentMileage))
            	    .orElse(fuelings.get(fuelings.size() - 1));

            int milesDriven = last.getCurrentMileage() - vehicle.getStartingMileage();
            double totalFuel = fuelings.stream().mapToDouble(FuelingEvent::getFuelAdded).sum();

            if (milesDriven > 0 && totalFuel > 0) {
                double mpg = milesDriven / totalFuel;
                result.add(new FuelEfficiencyDTO(vehicle.getVehicleNumber(), mpg));
            }
        }

        return result;
    }
}

