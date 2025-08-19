package com.leninalbino.inventory_system.service;

import com.leninalbino.inventory_system.model.dto.RegisterRequestDto;
import com.leninalbino.inventory_system.model.entity.User;

public interface AuthService {
    String getUserByToken(String document, String password);
    void registerUser(RegisterRequestDto dto);
    User getUser(String document, String password);
}
