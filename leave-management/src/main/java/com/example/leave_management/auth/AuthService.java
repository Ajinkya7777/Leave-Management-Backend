package com.example.leave_management.auth;


import com.example.leave_management.dto.AuthenticationRequest;
import com.example.leave_management.dto.AuthenticationResponse;
import com.example.leave_management.dto.RegisterRequest;

public interface AuthService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
