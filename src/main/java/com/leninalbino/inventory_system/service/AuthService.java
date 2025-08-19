package com.leninalbino.inventory_system.service;

import com.leninalbino.inventory_system.model.dto.RegisterRequestDto;

public interface AuthService {
    String getUserByToken(String document, String password);
    void registerUser(RegisterRequestDto dto);
}
