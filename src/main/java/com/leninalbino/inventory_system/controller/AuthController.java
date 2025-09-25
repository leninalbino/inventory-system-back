package com.leninalbino.inventory_system.controller;

import com.leninalbino.inventory_system.model.dto.ApiResponse;
import com.leninalbino.inventory_system.model.dto.LoginResponseDto;
import com.leninalbino.inventory_system.model.dto.RefreshResponseDto;
import com.leninalbino.inventory_system.model.dto.RegisterRequestDto;
import com.leninalbino.inventory_system.model.dto.UserRequestDto;
import com.leninalbino.inventory_system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "API para autenticación y gestión de usuarios")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @RequestBody @Valid UserRequestDto userRequestDto,
            @RequestHeader(value = "X-Device-ID", required = false) String deviceId,
            HttpServletResponse response) {
        
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = "device_" + System.currentTimeMillis();
        }
        
        LoginResponseDto loginResponse = authService.loginUser(
                userRequestDto.getDocument(), 
                userRequestDto.getPassword(), 
                deviceId
        );
        
        // Configurar cookies httpOnly
        Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // Solo HTTPS en producción
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); // 30 días
        response.addCookie(refreshTokenCookie);
        
        Cookie deviceIdCookie = new Cookie("deviceId", deviceId);
        deviceIdCookie.setHttpOnly(false); // Frontend necesita leer esto
        deviceIdCookie.setSecure(true);
        deviceIdCookie.setPath("/");
        deviceIdCookie.setMaxAge(30 * 24 * 60 * 60);
        response.addCookie(deviceIdCookie);
        
        // No enviar refreshToken en el response JSON por seguridad
        loginResponse.setRefreshToken(null);
        
        ApiResponse<LoginResponseDto> apiResponse = new ApiResponse<>(true, "Login successful", loginResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody @Valid RegisterRequestDto dto) {
        authService.registerUser(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuario registrado exitosamente", null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponseDto>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        
        String refreshToken = getRefreshTokenFromCookies(request);
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "No refresh token found", null));
        }
        
        try {
            RefreshResponseDto refreshResponse = authService.refreshToken(refreshToken);
            
            // Actualizar cookie con nuevo refreshToken
            Cookie newRefreshTokenCookie = new Cookie("refreshToken", refreshResponse.getRefreshToken());
            newRefreshTokenCookie.setHttpOnly(true);
            newRefreshTokenCookie.setSecure(true);
            newRefreshTokenCookie.setPath("/");
            newRefreshTokenCookie.setMaxAge(30 * 24 * 60 * 60);
            response.addCookie(newRefreshTokenCookie);
            
            // No enviar refreshToken en response
            refreshResponse.setRefreshToken(null);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed", refreshResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, String>> validateToken(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Primero intentar validar el token existente si está presente
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String validatedToken = authService.validateToken(token);
                Map<String, String> response = new HashMap<>();
                response.put("token", validatedToken);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                // Token inválido, continuar para intentar refresh
            }
        }
        
        // Si no hay token válido, intentar refresh usando la cookie
        String refreshToken = getRefreshTokenFromCookies(request);
        if (refreshToken != null) {
            try {
                RefreshResponseDto refreshResponse = authService.refreshToken(refreshToken);
                Map<String, String> response = new HashMap<>();
                response.put("token", refreshResponse.getToken());
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        
        String refreshToken = getRefreshTokenFromCookies(request);
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }
        
        // Limpiar cookies
        clearCookie(response, "refreshToken");
        clearCookie(response, "deviceId");
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Logout successful", null));
    }

    @PostMapping("/force-device")
    public ResponseEntity<ApiResponse<String>> forceDeviceSession(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        
        String deviceId = request.get("deviceId");
        String userDocument = getCurrentUserDocument(httpRequest);
        
        if (userDocument != null && deviceId != null) {
            authService.forceDeviceSession(userDocument, deviceId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Device session forced", null));
        }
        
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "Invalid request", null));
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void clearCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String getCurrentUserDocument(HttpServletRequest request) {
        // Extraer usuario del token Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // Aquí deberías decodificar el JWT para obtener el document
                // Por simplicidad, retorno null por ahora
                return null;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
