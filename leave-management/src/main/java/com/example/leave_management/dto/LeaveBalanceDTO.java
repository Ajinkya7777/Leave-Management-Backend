package com.example.leave_management.dto;

import com.example.leave_management.enums.LeaveType;
import lombok.Data;

@Data
public class LeaveBalanceDTO {
    private Long employeeId;
    private LeaveType leaveType;
    private int totalLeaves;
    private int remainingLeaves;
}
