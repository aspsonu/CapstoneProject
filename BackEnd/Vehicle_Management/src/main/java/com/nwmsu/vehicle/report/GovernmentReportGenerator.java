package com.nwmsu.vehicle.report;

import com.nwmsu.vehicle.dto.GovernmentReportDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Component
public class GovernmentReportGenerator {

    public InputStream export(List<GovernmentReportDTO> data) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Government Report");

            // 🔹 Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle centerStyle = createCenterStyle(workbook);

            // 🔹 Row 0: Merged Main Headers
            Row headerRow1 = sheet.createRow(0);
            headerRow1.setHeightInPoints(25);
            headerRow1.createCell(0).setCellValue("Vehicle Type");
            headerRow1.createCell(1).setCellValue("Description");
            headerRow1.createCell(2).setCellValue("Number of Vehicles Operated");
            headerRow1.createCell(4).setCellValue("Miles Travelled");
            headerRow1.createCell(5).setCellValue("Fuel Usage");
            headerRow1.createCell(7).setCellValue("Cost");

            // 🔹 Row 1: Sub-Headers
            Row headerRow2 = sheet.createRow(1);
            headerRow2.createCell(2).setCellValue("<= 8500 lbs");
            headerRow2.createCell(3).setCellValue("> 8500 lbs");
            headerRow2.createCell(5).setCellValue("Gas or Diesel");
            headerRow2.createCell(6).setCellValue("Alternative Fuel");
            headerRow2.createCell(7).setCellValue("Gas or Diesel");
            headerRow2.createCell(8).setCellValue("Alternative Fuel");
            headerRow2.createCell(9).setCellValue("Maintenance");

            // 🔹 Merge the header cells
            sheet.addMergedRegion(CellRangeAddress.valueOf("A1:A2"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("B1:B2"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("C1:D1"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("E1:E2"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("F1:G1"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("H1:J1"));
            
            sheet.addMergedRegion(CellRangeAddress.valueOf("A3:A5"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("A6:A8"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("A9:A11"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("A12:A14"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("A15:A17"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("A18:A20"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("A21:A23"));

            // 🔹 Apply styles
            for (int i = 0; i <= 9; i++) {
                headerRow1.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellStyle(headerStyle);
                headerRow2.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellStyle(headerStyle);
            }

            // 🔹 Fill data
            int rowIdx = 2;
            for (GovernmentReportDTO dto : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getVehicleType());
                row.createCell(1).setCellValue(dto.getVehicleDescription());
                
                /*row.createCell(2).setCellValue(dto.getLessThan8500());
                row.createCell(3).setCellValue(dto.getGreaterThan8500());
                row.createCell(4).setCellValue(dto.getMilesTravelled());
                row.createCell(5).setCellValue(dto.getGasOrDieselGallons());
                row.createCell(6).setCellValue(dto.getAltFuelGallons());
                row.createCell(7).setCellValue(dto.getGasOrDieselCost());
                row.createCell(8).setCellValue(dto.getAltFuelCost());
                row.createCell(9).setCellValue(dto.getMaintenanceCost());*/
                
                writeNumberOrBlank(row, 2, dto.getLessThan8500(), centerStyle);
                writeNumberOrBlank(row, 3, dto.getGreaterThan8500(), centerStyle);
                writeNumberOrBlank(row, 4, dto.getMilesTravelled(), centerStyle);
                writeNumberOrBlank(row, 5, dto.getGasOrDieselGallons(), centerStyle);
                writeNumberOrBlank(row, 6, dto.getAltFuelGallons(), centerStyle);
                writeNumberOrBlank(row, 7, dto.getGasOrDieselCost(), centerStyle);
                writeNumberOrBlank(row, 8, dto.getAltFuelCost(), centerStyle);
                writeNumberOrBlank(row, 9, dto.getMaintenanceCost(), centerStyle);

                for (int col = 0; col <= 9; col++) {
                    row.getCell(col).setCellStyle(centerStyle);
                }
                
                for (int i = 2; i <= 22; i++) { // A3 = row 2, A21 = row 20 (0-indexed), so +1 to include A21 = row 21
                    Row curretRow = sheet.getRow(i);
                    if (row != null) {
                        Cell cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellStyle(headerStyle); // Apply bold to column A
                    }
                }
            }

            // 🔹 Autosize all columns
            for (int i = 0; i <= 9; i++) {
                sheet.autoSizeColumn(i);
            }
            
            sheet.setColumnWidth(4, 5000);

            // 🔹 Export
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Failed to export Government Report to Excel", e);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createCenterStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private void writeNumberOrBlank(Row row, int columnIndex, double value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (value != 0) {
            cell.setCellValue(value);
        } else {
            cell.setCellValue(""); // Leave it blank
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

}
