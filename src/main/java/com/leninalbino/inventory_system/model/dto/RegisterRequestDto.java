package com.leninalbino.inventory_system.model.dto;

import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequestDto {
    private String document;
    private String password;
    private String username;
    private Set<String> roles; // Ejemplo: ["ADMIN", "EMPLOYEE"]
}
