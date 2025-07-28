package com.example.leave_management.controller;

import com.example.leave_management.dto.GroupedLeavesByStatusDTO;
import com.example.leave_management.dto.GroupedLeavesDTO;
import com.example.leave_management.dto.LeaveRequestDTO;
import com.example.leave_management.dto.LeaveResponseDTO;
import com.example.leave_management.enums.LeaveStatus;
import com.example.leave_management.service.EmailService;
import com.example.leave_management.service.LeaveRequestService;
import com.example.leave_management.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private EmailService emailService;



    @PostMapping
    public LeaveRequestDTO applyLeave(@RequestBody @Valid LeaveRequestDTO dto) {
        return leaveRequestService.applyLeave(dto);
    }

    @GetMapping
    public List<LeaveResponseDTO> getAllLeaves() {
        return leaveRequestService.getAllLeaves();
    }

    @PatchMapping("/{leaveId}/status")
    public LeaveResponseDTO updateLeaveStatus(
            @PathVariable Long leaveId,
            @RequestParam LeaveStatus status
    ) {
        return leaveRequestService.updateLeaveStatus(leaveId, status);
    }

    @GetMapping("/status")
    public List<LeaveResponseDTO> getLeavesByStatus(@RequestParam LeaveStatus status) {
        return leaveRequestService.getLeavesByStatus(status);
    }

    @GetMapping("/employee")
    public List<LeaveResponseDTO> getLeavesByEmployee(@RequestParam Long employeeId) {
        return leaveRequestService.getLeavesByEmployee(employeeId);
    }

    @GetMapping("/group-by-type")
    public GroupedLeavesDTO groupLeavesByType() {
        return leaveRequestService.groupLeavesByType();
    }

    @GetMapping("/group-by-status")
    public GroupedLeavesByStatusDTO groupLeavesByStatus() {
        return leaveRequestService.groupLeavesByStatus();
    }

    @GetMapping("/report")
    public ResponseEntity<byte[]> downloadLeaveReport() throws IOException {
        List<LeaveResponseDTO> leaves = leaveRequestService.getAllLeaves();
        ByteArrayOutputStream out = reportService.generateLeaveReportExcel(leaves);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=leave_report.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }


    @GetMapping("/report/email")
    public ResponseEntity<String> emailLeaveReport() {
        try {
            List<LeaveResponseDTO> leaves = leaveRequestService.getAllLeaves();
            ByteArrayOutputStream reportStream = reportService.generateLeaveReportExcel(leaves);

            String recipient = "manager@example.com"; // change to real/test email
            emailService.sendEmailWithAttachment(
                    recipient,
                    "Leave Report",
                    "Please find attached the latest leave report.",
                    reportStream,
                    "leave_report.xlsx"
            );

            return ResponseEntity.ok("Email sent successfully to " + recipient);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }

    @GetMapping("/my-leaves")
    public List<LeaveResponseDTO> getMyLeaves(Authentication authentication) {
        String email = authentication.getName(); // Extract email from JWT
        return leaveRequestService.getLeavesForAuthenticatedUser(email);
    }

}
