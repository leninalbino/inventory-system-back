package com.leninalbino.inventory_system.service;

public interface AuthService {
    String getUserByToken(String document, String password);
}
