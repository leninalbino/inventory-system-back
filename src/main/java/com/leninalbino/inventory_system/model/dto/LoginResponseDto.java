package com.leninalbino.inventory_system.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponseDto {
 private String token;
 private String refreshToken;
 private String username;
 private List<String> roles;
}
