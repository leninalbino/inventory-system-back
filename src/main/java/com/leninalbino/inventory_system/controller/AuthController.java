package com.leninalbino.inventory_system.controller;

import com.leninalbino.inventory_system.model.dto.LoginResponseDto;
import com.leninalbino.inventory_system.model.dto.UserRequestDto;
import com.leninalbino.inventory_system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthService authService;

    public ResponseEntity<LoginResponseDto>Login(@RequestBody @Valid UserRequestDto userRequestDto) {
        LoginResponseDto loginResponseDto = authService;
    }
}
