package com.example.leave_management.service;

import com.example.leave_management.dto.EmployeeDTO;
import com.example.leave_management.entity.Department;
import com.example.leave_management.entity.Employee;
import com.example.leave_management.repository.DepartmentRepository;
import com.example.leave_management.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeDTO createEmployee(EmployeeDTO dto) {

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        Employee employee = Employee.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(encodedPassword)
                .department(department)
                .role(dto.getRole())
                .build();

        employee = employeeRepository.save(employee);

        return mapToDTO(employee);
    }

    public Page<EmployeeDTO> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    private EmployeeDTO mapToDTO(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .role(employee.getRole())
                .build();
    }


    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToDTO(employee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }


}
