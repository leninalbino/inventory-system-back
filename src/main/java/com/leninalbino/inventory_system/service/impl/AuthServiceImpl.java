package com.leninalbino.inventory_system.service.impl;

import com.leninalbino.inventory_system.model.dto.RegisterRequestDto;
import com.leninalbino.inventory_system.model.entity.User;
import com.leninalbino.inventory_system.repository.AuthRepository;
import com.leninalbino.inventory_system.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthRepository repository;
    private final String secretKey;

    public AuthServiceImpl(AuthRepository repository, @Value("${security.jwt.secret}") String secretKey) {
        this.repository = repository;
        this.secretKey = secretKey;
    }

    @Override
    public String getUserByToken(String document, String password) {
        User user = repository.findByDocumentAndPassword(document, password);
        if (user == null) {
            throw new RuntimeException("Credenciales inválidas");
        }

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + 3600000; // 1 hora

        return Jwts.builder()
                .setSubject(user.getDocument())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles())
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(new Date(expMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
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
