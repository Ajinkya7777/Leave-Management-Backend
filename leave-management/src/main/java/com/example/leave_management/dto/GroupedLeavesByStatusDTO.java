package com.example.leave_management.dto;

import com.example.leave_management.enums.LeaveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupedLeavesByStatusDTO {
    private Map<LeaveStatus, List<LeaveRequestDTO>> groupedByStatus;

}
