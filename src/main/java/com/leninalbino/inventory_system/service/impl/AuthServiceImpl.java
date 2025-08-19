package com.leninalbino.inventory_system.service.impl;

import com.leninalbino.inventory_system.model.entity.User;
import com.leninalbino.inventory_system.repository.AuthRepository;
import com.leninalbino.inventory_system.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

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

        return Jwts.builder()
                .setSubject(user.getDocument())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
