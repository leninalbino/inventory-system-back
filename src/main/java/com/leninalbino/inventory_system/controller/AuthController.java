package com.leninalbino.inventory_system.controller;

import com.leninalbino.inventory_system.model.dto.ApiResponse;
import com.leninalbino.inventory_system.model.dto.LoginResponseDto;
import com.leninalbino.inventory_system.model.dto.RegisterRequestDto;
import com.leninalbino.inventory_system.model.dto.UserRequestDto;
import com.leninalbino.inventory_system.model.entity.User;
import com.leninalbino.inventory_system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

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
        User user = authService.getUser(userRequestDto.getDocument(), userRequestDto.getPassword());
        String token = authService.getUserByToken(userRequestDto.getDocument(), userRequestDto.getPassword());
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRoles(new ArrayList<>(user.getRoles()));
        // Aquí podemos agregar más información al response si es necesario, como el usuario o roles
        ApiResponse<LoginResponseDto> apiResponse = new ApiResponse<>(true, "Login successful", response);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody @Valid RegisterRequestDto dto) {
        authService.registerUser(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuario registrado exitosamente", null));
    }
}
