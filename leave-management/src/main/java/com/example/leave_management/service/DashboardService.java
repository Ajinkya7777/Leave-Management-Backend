package com.example.leave_management.service;

import com.example.leave_management.dto.DashboardSummaryDTO;
import com.example.leave_management.entity.Employee;
import com.example.leave_management.entity.LeaveRequest;
import com.example.leave_management.enums.LeaveStatus;
import com.example.leave_management.enums.LeaveType;
import com.example.leave_management.repository.EmployeeRepository;
import com.example.leave_management.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    public DashboardSummaryDTO getDashboardSummary() {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();
        List<Employee> employees = employeeRepository.findAll();

        Map<LeaveStatus, Long> statusCounts = leaveRequests.stream()
                .collect(Collectors.groupingBy(LeaveRequest::getLeaveStatus, Collectors.counting()));

        Map<LeaveType, Long> typeCounts = leaveRequests.stream()
                .collect(Collectors.groupingBy(LeaveRequest::getLeaveType, Collectors.counting()));

        // Ensure all enums are present even if 0
        for (LeaveStatus status : LeaveStatus.values()) {
            statusCounts.putIfAbsent(status, 0L);
        }
        for (LeaveType type : LeaveType.values()) {
            typeCounts.putIfAbsent(type, 0L);
        }

        return DashboardSummaryDTO.builder()
                .totalEmployees(employees.size())
                .totalLeaveRequests(leaveRequests.size())
                .leavesByStatus(statusCounts)
                .leavesByType(typeCounts)
                .build();
    }
}
