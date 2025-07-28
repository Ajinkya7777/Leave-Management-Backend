package com.example.leave_management.service;

import com.example.leave_management.dto.LeaveResponseDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ByteArrayOutputStream generateLeaveReportExcel(List<LeaveResponseDTO> leaves) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Leave Report");

            // Header
            String[] columns = {"Employee Name", "Leave Type", "Start Date", "End Date", "Leave Status", "Reason"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Rows
            int rowIdx = 1;
            for (LeaveResponseDTO leave : leaves) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(leave.getEmployeeName());
                row.createCell(1).setCellValue(leave.getLeaveType().toString());
                row.createCell(2).setCellValue(leave.getStartDate().toString());
                row.createCell(3).setCellValue(leave.getEndDate().toString());
                row.createCell(4).setCellValue(leave.getLeaveStatus().toString());
                row.createCell(5).setCellValue(leave.getReason());
            }

            workbook.write(out);
            return out;
        }
    }

}
