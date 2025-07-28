package com.example.leave_management.controller;

import com.example.leave_management.dto.LeaveBalanceDTO;
import com.example.leave_management.service.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-balance")
public class LeaveBalanceController {

    @Autowired
    private LeaveBalanceService service;

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<LeaveBalanceDTO>> getLeaveBalances(
            @PathVariable Long employeeId,
            Authentication authentication
    ) {
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("USER");

        List<LeaveBalanceDTO> balances = service.getLeaveBalances(employeeId, role);
        return ResponseEntity.ok(balances);
    }


    @PostMapping("/init/{employeeId}")
    public ResponseEntity<String> initializeBalance(@PathVariable Long employeeId) {
        service.initializeLeaveBalance(employeeId);
        return ResponseEntity.ok("Leave balance initialized.");
    }
}
