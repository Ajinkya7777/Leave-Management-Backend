package com.example.leave_management.dto;

import com.example.leave_management.enums.LeaveStatus;
import com.example.leave_management.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardSummaryDTO {
    private long totalEmployees;
    private long totalLeaveRequests;
    private Map<LeaveStatus, Long> leavesByStatus;
    private Map<LeaveType, Long> leavesByType;
}

