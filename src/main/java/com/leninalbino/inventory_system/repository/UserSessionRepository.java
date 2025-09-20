package com.leninalbino.inventory_system.repository;

import com.leninalbino.inventory_system.model.entity.UserSession;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository {
    UserSession save(UserSession session);
    Optional<UserSession> findByRefreshToken(String refreshToken);
    List<UserSession> findActiveSessionsByUserDocument(String userDocument);
    void deleteByRefreshToken(String refreshToken);
    void deactivateAllUserSessions(String userDocument);
    void deactivateOtherDeviceSessions(String userDocument, String deviceId);
}