package com.leninalbino.inventory_system.model.dto;

import lombok.Data;

@Data
public class RefreshResponseDto {
    private String token;
    private String refreshToken;
}