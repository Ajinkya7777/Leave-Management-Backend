package com.example.leave_management.dto;

import com.example.leave_management.entity.Department;
import com.example.leave_management.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email should be valid")
    private String email;

    private Long departmentId;

    private Role role;
    private Long id;

    private String departmentName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


}
