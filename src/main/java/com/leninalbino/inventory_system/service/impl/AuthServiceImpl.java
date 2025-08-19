package com.leninalbino.inventory_system.service.impl;

import com.leninalbino.inventory_system.model.entity.User;
import com.leninalbino.inventory_system.repository.AuthRepository;
import com.leninalbino.inventory_system.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class AuthServiceImpl implements AuthService {

    private AuthRepository repository;
    private String secretKey;

    @Override
    public String getUserByToken(String document, String password) {
        User user = repository.getUserByToken(document, password);
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()

                .setSubject(user.getDocument())

                .signWith(key, SignatureAlgorithm.HS256)

                .compact();
    }
}
