package com.example.leave_management.service;

import com.example.leave_management.dto.GroupedLeavesByStatusDTO;
import com.example.leave_management.dto.GroupedLeavesDTO;
import com.example.leave_management.dto.LeaveRequestDTO;
import com.example.leave_management.dto.LeaveResponseDTO;
import com.example.leave_management.entity.Employee;
import com.example.leave_management.entity.LeaveRequest;
import com.example.leave_management.enums.LeaveStatus;
import com.example.leave_management.enums.LeaveType;
import com.example.leave_management.exception.ResourceNotFoundException;
import com.example.leave_management.repository.EmployeeRepository;
import com.example.leave_management.repository.LeaveRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LeaveRequestService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestService.class);

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private EmailService emailService;


    // Apply for leave - create new leave request
    public LeaveRequestDTO applyLeave(LeaveRequestDTO dto) {
        logger.info("Applying leave for employeeId={}", dto.getEmployeeId());

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + dto.getEmployeeId()));

        LeaveRequest leaveRequest = mapToEntity(dto, employee);
        leaveRequest.setLeaveStatus(LeaveStatus.PENDING); // default status on creation

        leaveRequest = leaveRequestRepository.save(leaveRequest);

        // Send email notification
        String to = employee.getEmail();
        String subject = "Leave Application Received";
        String text = "Hi " + employee.getName() + ",\nYour leave request from "
                + dto.getStartDate() + " to " + dto.getEndDate() + " has been received.";
        emailService.sendSimpleEmail(to, subject, text);

        return mapToDTO(leaveRequest);
    }

    // Get all leave requests as response DTOs
    public List<LeaveResponseDTO> getAllLeaves() {
        logger.info("Fetching all leave requests");
        return leaveRequestRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Update leave status by leaveRequestId
    public LeaveResponseDTO updateLeaveStatus(Long leaveRequestId, LeaveStatus newStatus) {
        logger.info("Updating leave status for leaveRequestId={} to {}", leaveRequestId, newStatus);

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Request not found with id " + leaveRequestId));

        leaveRequest.setLeaveStatus(newStatus);
        LeaveRequest updatedLeave = leaveRequestRepository.save(leaveRequest);

        return mapToResponseDTO(updatedLeave);
    }

    // Get leaves filtered by status
    public List<LeaveResponseDTO> getLeavesByStatus(LeaveStatus status) {
        logger.info("Fetching leave requests with status {}", status);

        return leaveRequestRepository.findAll().stream()
                .filter(leave -> leave.getLeaveStatus() == status)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Get leaves filtered by employeeId
    public List<LeaveResponseDTO> getLeavesByEmployee(Long employeeId) {
        logger.info("Fetching leave requests for employeeId={}", employeeId);

        return leaveRequestRepository.findAll().stream()
                .filter(leave -> leave.getEmployee().getId().equals(employeeId))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Group leaves by LeaveType returning GroupedLeavesDTO
    public GroupedLeavesDTO groupLeavesByType() {
        logger.info("Grouping leave requests by leave type");

        List<LeaveRequest> leaves = leaveRequestRepository.findAll();

        Map<LeaveType, List<LeaveResponseDTO>> grouped = leaves.stream()
                .collect(Collectors.groupingBy(
                        LeaveRequest::getLeaveType,
                        Collectors.mapping(this::mapToResponseDTO, Collectors.toList())
                ));

        return GroupedLeavesDTO.builder()
                .groupedByType(grouped)
                .build();
    }

    // Group leaves by LeaveStatus returning GroupedLeavesByStatusDTO
    public GroupedLeavesByStatusDTO groupLeavesByStatus() {
        logger.info("Grouping leave requests by leave status");

        List<LeaveRequest> leaves = leaveRequestRepository.findAll();

        Map<LeaveStatus, List<LeaveRequestDTO>> grouped = leaves.stream()
                .collect(Collectors.groupingBy(
                        LeaveRequest::getLeaveStatus,
                        Collectors.mapping(this::mapToDTO, Collectors.toList())
                ));

        // Ensure all LeaveStatus keys exist even if empty lists
        for (LeaveStatus status : LeaveStatus.values()) {
            grouped.putIfAbsent(status, new ArrayList<>());
        }

        return GroupedLeavesByStatusDTO.builder()
                .groupedByStatus(grouped)
                .build();
    }

    // ----------------- Helper Mapping Methods -----------------

    // Map LeaveRequest entity → LeaveResponseDTO (used for responses)
    private LeaveResponseDTO mapToResponseDTO(LeaveRequest leave) {
        return LeaveResponseDTO.builder()
                .leaveId(leave.getId())
                .employeeName(leave.getEmployee().getName())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .leaveType(leave.getLeaveType())
                .leaveStatus(leave.getLeaveStatus())
                .build();
    }

    // Map LeaveRequestDTO + Employee entity → LeaveRequest entity (used for creation)
    private LeaveRequest mapToEntity(LeaveRequestDTO dto, Employee employee) {
        return LeaveRequest.builder()
                .id(dto.getId())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .reason(dto.getReason())
                .leaveType(dto.getLeaveType())
                .employee(employee)
                // leaveStatus is set separately on creation (PENDING)
                .build();
    }

    // Map LeaveRequest entity → LeaveRequestDTO (used for grouping by status, includes employeeName and leaveStatus as String)
    private LeaveRequestDTO mapToDTO(LeaveRequest leave) {
        return LeaveRequestDTO.builder()
                .id(leave.getId())
                .employeeId(leave.getEmployee().getId())
                .employeeName(leave.getEmployee().getName())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .leaveType(leave.getLeaveType())
                .leaveStatus(leave.getLeaveStatus().name())  // convert enum to String
                .build();
    }

    public List<LeaveResponseDTO> getLeavesForAuthenticatedUser(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByEmployee(employee);
        return leaveRequests.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


}
