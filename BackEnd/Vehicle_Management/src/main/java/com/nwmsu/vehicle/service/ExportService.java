package com.nwmsu.vehicle.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.nwmsu.vehicle.dto.MaintenanceReportDTO;
import com.nwmsu.vehicle.dto.VehicleReportDTO;


@Service
public class ExportService {

    @Autowired
    private ReportService reportService;

    // Export Maintenance Report to PDF (Using iText 5)
    /*public byte[] exportMaintenanceToPDF() throws Exception {
        List<MaintenanceReportDTO> maintenanceEvents = reportService.getAllMaintenanceEvents();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD); // Explicitly using iText Font
        Paragraph title = new Paragraph("Northwest Missouri State University", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(new Paragraph("Maintenance Events Report", titleFont));
        document.add(title);
        document.add(new Paragraph("\n"));

        // Create Table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 3, 3, 6});

        // Add Header Row
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD); // Explicitly using iText Font
        table.addCell(new PdfPCell(new Phrase("Vehicle Number", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Date", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Maintenance Cost ($)", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Description", headerFont)));

        // Fill Data
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 10); // Explicitly using iText Font
        for (MaintenanceReportDTO event : maintenanceEvents) {
            table.addCell(new PdfPCell(new Phrase(event.getVehicleNumber(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(event.getDate().toString(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(event.getMaintenanceCost()), cellFont)));
            table.addCell(new PdfPCell(new Phrase(event.getMaintenanceDescription(), cellFont)));
        }

        document.add(table);
        document.close();
        return outputStream.toByteArray();
    } */
    
    public byte[] exportMaintenanceToPDF() throws Exception {
        List<MaintenanceReportDTO> maintenanceEvents = reportService.getAllMaintenanceEvents();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();

        // Logo (adjust path and size as needed)
        Image logo = Image.getInstance("src/main/resources/static/NwmsuLogo.png"); // Adjust the path
        logo.scaleAbsolute(60f, 60f); // Resize logo
        logo.setAlignment(Image.ALIGN_LEFT);

        // Fonts
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);

        // Create a table for logo and titles
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 4});

        // Logo Cell
        PdfPCell logoCell = new PdfPCell(logo);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setRowspan(2);
        headerTable.addCell(logoCell);

        // Title Cell
        PdfPCell titleCell = new PdfPCell(new Phrase("Northwest Missouri State University", titleFont));
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(titleCell);

        // Subtitle Cell
        PdfPCell subtitleCell = new PdfPCell(new Phrase("Maintenance Report", subTitleFont));
        subtitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        subtitleCell.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(subtitleCell);

        // Add spacing below header
        document.add(headerTable);
        document.add(Chunk.NEWLINE);

        // Create Table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 3, 3, 6});

        // Header Row
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        table.addCell(new PdfPCell(new Phrase("Vehicle Number", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Date", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Maintenance Cost ($)", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Description", headerFont)));

        // Data Rows
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);
        for (MaintenanceReportDTO event : maintenanceEvents) {
            table.addCell(new PdfPCell(new Phrase(event.getVehicleNumber(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(event.getDate().toString(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(event.getMaintenanceCost()), cellFont)));
            table.addCell(new PdfPCell(new Phrase(event.getMaintenanceDescription(), cellFont)));
        }

        document.add(table);
        document.close();
        return outputStream.toByteArray();
    }

    
    /*public InputStream exportFilteredMaintenanceEvents(String vehicleNumber, LocalDate startDate,
            LocalDate endDate, Double minCost, Double maxCost) {
		List<MaintenanceReportDTO> filteredData = reportService.filterMaintenanceEvents(
		vehicleNumber, startDate, endDate, minCost, maxCost
		);
		
		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
		PdfWriter.getInstance(document, out);
		document.open();
		
		Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
		document.add(new Paragraph("Filtered Maintenance Events Report", titleFont));
		document.add(Chunk.NEWLINE);
		
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		Stream.of("Date", "Vehicle Number", "Cost", "Description")
		.forEach(header -> {
		PdfPCell cell = new PdfPCell(new Phrase(header));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);
		});
		
		for (MaintenanceReportDTO dto : filteredData) {
		table.addCell(dto.getDate().toString());
		table.addCell(dto.getVehicleNumber());
		table.addCell(String.valueOf(dto.getMaintenanceCost()));
		table.addCell(dto.getMaintenanceDescription());
		}
		
		document.add(table);
		document.close();
		
		} catch (Exception e) {
		throw new RuntimeException("Failed to export maintenance report", e);
		}
		
		return new ByteArrayInputStream(out.toByteArray());
	} */
    
    public InputStream exportFilteredMaintenanceEvents(String vehicleNumber, LocalDate startDate,
            LocalDate endDate, Double minCost, Double maxCost) {
        List<MaintenanceReportDTO> filteredData = reportService.filterMaintenanceEvents(
                vehicleNumber, startDate, endDate, minCost, maxCost
        );

        // Add top/left/right/bottom margins inside the page
        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Define visible page border
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    PdfContentByte canvas = writer.getDirectContent();
                    canvas.setLineWidth(1f);
                    Rectangle rect = new Rectangle(document.left() - 10, document.bottom() - 10,
                                                   document.right() + 10, document.top() + 10);
                    canvas.rectangle(rect);
                    canvas.stroke();
                }
            });

            document.open();

            // Load and size the logo
            Image logo = Image.getInstance("src/main/resources/static/NWLogo.png");
            logo.scaleAbsolute(60f, 60f); // resize logo
            logo.setSpacingBefore(10f);
            logo.setSpacingAfter(10f);
            logo.setAlignment(Image.ALIGN_LEFT);

            // Fonts
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
            Font subTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);

            // Header table with logo + titles
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 4});
            headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setRowspan(2);
            logoCell.setPadding(5f); // adds space between logo and border
            headerTable.addCell(logoCell);

            // Title (slightly left-aligned but centered in visual space)
            PdfPCell titleCell = new PdfPCell(new Phrase("Northwest Missouri State University", titleFont));
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setPaddingTop(10f);
            headerTable.addCell(titleCell);

            PdfPCell subTitleCell = new PdfPCell(new Phrase("Vehicle Maintenance Report", subTitleFont));
            subTitleCell.setBorder(Rectangle.NO_BORDER);
            subTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            subTitleCell.setPaddingBottom(10f);
            headerTable.addCell(subTitleCell);

            // Add to document
            document.add(headerTable);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE); // Add extra space before table

            // Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 3, 2, 6});
            table.setSpacingBefore(10f); // move table further down

            Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Stream.of("Date", "Vehicle Number", "Cost ($)", "Description")
                    .forEach(header -> {
                        PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        table.addCell(cell);
                    });

            Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);
            for (MaintenanceReportDTO dto : filteredData) {
                table.addCell(new PdfPCell(new Phrase(dto.getDate().toString(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(dto.getVehicleNumber(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(dto.getMaintenanceCost()), cellFont)));
                table.addCell(new PdfPCell(new Phrase(dto.getMaintenanceDescription(), cellFont)));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export maintenance report", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
    
    public InputStream exportFilteredVehicleMileage(String vehicleNumber, Integer minMileage, Integer maxMileage) {
        List<VehicleReportDTO> data = reportService.filterVehiclesWithMileage(vehicleNumber, minMileage, maxMileage);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Mileage Report");

            org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(boldFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            Row titleRow1 = sheet.createRow(0);
            Cell titleCell1 = titleRow1.createCell(0);
            titleCell1.setCellValue("Northwest Missouri State University");
            titleCell1.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

            Row titleRow2 = sheet.createRow(1);
            Cell titleCell2 = titleRow2.createCell(0);
            titleCell2.setCellValue("Vehicle Mileage Report");
            titleCell2.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));

            Row header = sheet.createRow(2);
            String[] headers = {"Vehicle Number", "Model Year", "Current Mileage"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 3;
            for (VehicleReportDTO dto : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getVehicleNumber());
                row.createCell(1).setCellValue(dto.getModelYear());
                row.createCell(2).setCellValue(dto.getCurrentMileage());
            }

            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }
}
