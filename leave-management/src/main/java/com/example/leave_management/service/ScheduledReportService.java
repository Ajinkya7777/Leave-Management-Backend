package com.example.leave_management.service;

import com.example.leave_management.dto.LeaveResponseDTO;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class ScheduledReportService {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private EmailService emailService;

    // Runs every Monday at 8:00 AM
    @Scheduled(cron = "0 0 8 * * MON")
    public void sendWeeklyLeaveReport() {
        log.info("Running scheduled task: sendWeeklyLeaveReport");

        try {
            List<LeaveResponseDTO> leaves = leaveRequestService.getAllLeaves();
            ByteArrayOutputStream report = reportService.generateLeaveReportExcel(leaves);

            emailService.sendEmailWithAttachment(
                    "manager@example.com",
                    "Weekly Leave Report",
                    "Please find attached the weekly leave report.",
                    report,
                    "leave_report.xlsx"
            );

            log.info("Leave report emailed successfully.");
        } catch (IOException | MessagingException e) {
            log.error("Error sending scheduled leave report: ", e);
        }
    }
}

