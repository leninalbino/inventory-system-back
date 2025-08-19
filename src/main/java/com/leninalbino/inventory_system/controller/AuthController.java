package com.leninalbino.inventory_system.controller;

import com.leninalbino.inventory_system.model.dto.ApiResponse;
import com.leninalbino.inventory_system.model.dto.LoginResponseDto;
import com.leninalbino.inventory_system.model.dto.UserRequestDto;
import com.leninalbino.inventory_system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody @Valid UserRequestDto userRequestDto) {
        String token = authService.getUserByToken(userRequestDto.getDocument(), userRequestDto.getPassword());
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        // Aquí podemos agregar más información al response si es necesario, como el usuario o roles
        ApiResponse<LoginResponseDto> apiResponse = new ApiResponse<>(true, "Login successful", response);
        return ResponseEntity.ok(apiResponse);
    }
}
