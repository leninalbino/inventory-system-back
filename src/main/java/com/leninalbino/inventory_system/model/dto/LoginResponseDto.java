package com.leninalbino.inventory_system.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponseDto {
 private String token;
 private String username;
 List<String> roles;
}
