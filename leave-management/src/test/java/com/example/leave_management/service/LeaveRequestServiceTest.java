package com.example.leave_management.service;

import com.example.leave_management.dto.LeaveRequestDTO;
import com.example.leave_management.dto.LeaveResponseDTO;
import com.example.leave_management.entity.Employee;
import com.example.leave_management.entity.LeaveRequest;
import com.example.leave_management.enums.LeaveStatus;
import com.example.leave_management.enums.LeaveType;
import com.example.leave_management.repository.EmployeeRepository;
import com.example.leave_management.repository.LeaveRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class LeaveRequestServiceTest {

    @InjectMocks
    private LeaveRequestService leaveRequestService;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    private Employee mockEmployee;
    private LeaveRequestDTO mockLeaveDTO;
    private LeaveRequest mockSavedLeave;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 🧱 Common mock Employee
        mockEmployee = new Employee();
        mockEmployee.setId(1L);
        mockEmployee.setName("Ajinkya");

        // 🧱 Common input DTO
        mockLeaveDTO = LeaveRequestDTO.builder()
                .employeeId(1L)
                .startDate(LocalDate.of(2025, 7, 10))
                .endDate(LocalDate.of(2025, 7, 15))
                .reason("Medical leave")
                .leaveType(LeaveType.SICK)
                .build();

        // 🧱 Simulated saved LeaveRequest
        mockSavedLeave = new LeaveRequest();
        mockSavedLeave.setId(101L);
        mockSavedLeave.setEmployee(mockEmployee);
        mockSavedLeave.setStartDate(mockLeaveDTO.getStartDate());
        mockSavedLeave.setEndDate(mockLeaveDTO.getEndDate());
        mockSavedLeave.setReason(mockLeaveDTO.getReason());
        mockSavedLeave.setLeaveType(mockLeaveDTO.getLeaveType());
        mockSavedLeave.setLeaveStatus(LeaveStatus.PENDING);
    }

    @Test
    void testApplyLeave_shouldReturnDTO() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(mockSavedLeave);

        // Act
        LeaveRequestDTO response = leaveRequestService.applyLeave(mockLeaveDTO);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEmployeeId()).isEqualTo(mockEmployee.getId());
        assertThat(response.getReason()).isEqualTo(mockLeaveDTO.getReason());
        assertThat(response.getLeaveType()).isEqualTo(mockLeaveDTO.getLeaveType());

        verify(employeeRepository, times(1)).findById(1L);
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
    }

    @Test
    void testUpdateLeaveStatus_shouldUpdateStatus() {
        LeaveRequest leaveRequest = LeaveRequest.builder()
                .id(1L)
                .leaveStatus(LeaveStatus.PENDING)
                .employee(mockEmployee)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .reason("Test")
                .leaveType(LeaveType.SICK)
                .build();

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveResponseDTO response = leaveRequestService.updateLeaveStatus(1L, LeaveStatus.APPROVED);

        assertThat(response.getLeaveStatus()).isEqualTo(LeaveStatus.APPROVED);
        verify(leaveRequestRepository, times(1)).findById(1L);
        verify(leaveRequestRepository, times(1)).save(leaveRequest);
    }



    // 🧪 More test methods like updateStatus, grouping etc. can reuse this setup.
}
