package com.example.leave_management.dto;

import com.example.leave_management.enums.LeaveType;
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
public class GroupedLeavesDTO {
    private Map<LeaveType, List<LeaveResponseDTO>> groupedByType;
}
