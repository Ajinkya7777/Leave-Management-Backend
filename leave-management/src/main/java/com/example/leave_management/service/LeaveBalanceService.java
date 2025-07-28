package com.example.leave_management.service;

import com.example.leave_management.dto.LeaveBalanceDTO;
import com.example.leave_management.entity.LeaveBalance;
import com.example.leave_management.enums.LeaveType;
import com.example.leave_management.repository.LeaveBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveBalanceService {

    @Autowired
    private LeaveBalanceRepository repository;

    public List<LeaveBalanceDTO> getLeaveBalances(Long employeeId, String role) {
        List<LeaveBalance> balances;

        if (role.equalsIgnoreCase("ADMIN")) {
            balances = repository.findAll();
        } else {
            balances = repository.findByEmployeeId(employeeId);
        }

        return balances.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public void initializeLeaveBalance(Long employeeId) {
        for (LeaveType type : LeaveType.values()) {
            LeaveBalance balance = new LeaveBalance();
            balance.setEmployeeId(employeeId);
            balance.setLeaveType(type);
            balance.setTotalLeaves(10); // fixed leave quota
            balance.setRemainingLeaves(10);
            repository.save(balance);
        }
    }

    private LeaveBalanceDTO convertToDTO(LeaveBalance balance) {
        LeaveBalanceDTO dto = new LeaveBalanceDTO();
        dto.setEmployeeId(balance.getEmployeeId());
        dto.setLeaveType(balance.getLeaveType());
        dto.setTotalLeaves(balance.getTotalLeaves());
        dto.setRemainingLeaves(balance.getRemainingLeaves());
        return dto;
    }
}
