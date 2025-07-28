package com.example.leave_management.dto;

import com.example.leave_management.enums.LeaveStatus;
import com.example.leave_management.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveResponseDTO {

    private String employeeName;
    private Long leaveId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveType leaveType;
    private LeaveStatus leaveStatus;
}
