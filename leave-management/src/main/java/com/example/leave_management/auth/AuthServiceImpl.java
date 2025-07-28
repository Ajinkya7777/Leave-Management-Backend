package com.example.leave_management.auth;

import com.example.leave_management.dto.*;
import com.example.leave_management.entity.Department;
import com.example.leave_management.entity.Employee;
import com.example.leave_management.enums.Role;
import com.example.leave_management.repository.DepartmentRepository;
import com.example.leave_management.repository.EmployeeRepository;
import com.example.leave_management.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        Department dept = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Employee employee = Employee.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .department(dept)
                .role(request.getRole())
                .active(true)
                .build();

        employeeRepository.save(employee);

        String token = jwtService.generateToken(employee);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(employee);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }
}
