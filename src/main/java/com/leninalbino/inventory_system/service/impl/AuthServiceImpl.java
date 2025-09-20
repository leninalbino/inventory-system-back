package com.leninalbino.inventory_system.service.impl;

import com.leninalbino.inventory_system.model.dto.LoginResponseDto;
import com.leninalbino.inventory_system.model.dto.RefreshResponseDto;
import com.leninalbino.inventory_system.model.dto.RegisterRequestDto;
import com.leninalbino.inventory_system.model.entity.User;
import com.leninalbino.inventory_system.model.entity.UserSession;
import com.leninalbino.inventory_system.repository.AuthRepository;
import com.leninalbino.inventory_system.repository.UserSessionRepository;
import com.leninalbino.inventory_system.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthRepository repository;
    private final UserSessionRepository sessionRepository;
    private final String secretKey;

    public AuthServiceImpl(AuthRepository repository, UserSessionRepository sessionRepository, @Value("${security.jwt.secret}") String secretKey) {
        this.repository = repository;
        this.sessionRepository = sessionRepository;
        this.secretKey = secretKey;
    }

    @Override
    public LoginResponseDto loginUser(String document, String password, String deviceId) {
        User user = repository.findByDocumentAndPassword(document, password);
        if (user == null) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Verificar si hay otras sesiones activas para este dispositivo
        checkExistingDeviceSessions(user.getDocument(), deviceId);

        String token = generateAccessToken(user);
        String refreshToken = generateRefreshToken();
        
        // Crear sesión de usuario
        UserSession session = new UserSession();
        session.setUserDocument(user.getDocument());
        session.setRefreshToken(refreshToken);
        session.setDeviceId(deviceId);
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusDays(30)); // 30 días
        sessionRepository.save(session);

        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setUsername(user.getUsername());
        response.setRoles(new ArrayList<>(user.getRoles()));
        
        return response;
    }

    @Override
    public RefreshResponseDto refreshToken(String refreshToken) {
        Optional<UserSession> sessionOpt = sessionRepository.findByRefreshToken(refreshToken);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("Refresh token inválido o expirado");
        }

        UserSession session = sessionOpt.get();
        User user = repository.findByDocument(session.getUserDocument());
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Generar nuevos tokens
        String newAccessToken = generateAccessToken(user);
        String newRefreshToken = generateRefreshToken();

        // Actualizar sesión
        session.setRefreshToken(newRefreshToken);
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusDays(30));
        sessionRepository.save(session);

        RefreshResponseDto response = new RefreshResponseDto();
        response.setToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        
        return response;
    }

    @Override
    public String validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return token; // Token válido
        } catch (Exception e) {
            throw new RuntimeException("Token inválido");
        }
    }

    @Override
    public void logout(String refreshToken) {
        sessionRepository.deleteByRefreshToken(refreshToken);
    }

    @Override
    public void forceDeviceSession(String userDocument, String deviceId) {
        sessionRepository.deactivateOtherDeviceSessions(userDocument, deviceId);
    }

    private String generateAccessToken(User user) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + 3600000; // 1 hora

        return Jwts.builder()
                .setSubject(user.getDocument())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(new Date(expMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    private void checkExistingDeviceSessions(String userDocument, String deviceId) {
        var activeSessions = sessionRepository.findActiveSessionsByUserDocument(userDocument);
        for (UserSession session : activeSessions) {
            if (!session.getDeviceId().equals(deviceId)) {
                // Hay otra sesión activa en otro dispositivo
                // Por ahora permitimos múltiples sesiones, pero el frontend manejará la alerta
            }
        }
    }

    @Override
    public void registerUser(RegisterRequestDto dto) {
        if (repository.findByDocumentAndPassword(dto.getDocument(), dto.getPassword()) != null) {
            throw new RuntimeException("Usuario ya existe");
        }
        User user = new User();
        user.setDocument(dto.getDocument());
        user.setPassword(dto.getPassword()); // Reemplaza por encoder si usas hash
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRoles(dto.getRoles());
        repository.save(user);
    }

    @Override
    public User getUser(String document, String password) {
        User user = repository.findByDocumentAndPassword(document, password);
        if (user == null) {
            throw new RuntimeException("Credenciales inválidas");
        }
        return user;
    }
}
