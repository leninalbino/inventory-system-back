package com.leninalbino.inventory_system.service;

import com.leninalbino.inventory_system.model.dto.LoginResponseDto;
import com.leninalbino.inventory_system.model.dto.RefreshResponseDto;
import com.leninalbino.inventory_system.model.dto.RegisterRequestDto;
import com.leninalbino.inventory_system.model.entity.User;

public interface AuthService {
    LoginResponseDto loginUser(String document, String password, String deviceId);
    RefreshResponseDto refreshToken(String refreshToken);
    String validateToken(String token);
    void logout(String refreshToken);
    void forceDeviceSession(String userDocument, String deviceId);
    void registerUser(RegisterRequestDto dto);
    User getUser(String document, String password);
}
