package com.leninalbino.inventory_system.repository;

import com.leninalbino.inventory_system.model.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository para gestionar sesiones de usuario con Spring Data JPA
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    
    /**
     * Busca una sesión por refresh token
     */
    Optional<UserSession> findByRefreshToken(String refreshToken);
    
    /**
     * Busca sesiones activas por documento de usuario
     */
    @Query("SELECT us FROM UserSession us WHERE us.userDocument = :userDocument AND us.active = true")
    List<UserSession> findActiveSessionsByUserDocument(@Param("userDocument") String userDocument);
    
    /**
     * Elimina sesión por refresh token
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserSession us WHERE us.refreshToken = :refreshToken")
    void deleteByRefreshToken(@Param("refreshToken") String refreshToken);
    
    /**
     * Desactiva todas las sesiones de un usuario
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserSession us SET us.active = false WHERE us.userDocument = :userDocument")
    void deactivateAllUserSessions(@Param("userDocument") String userDocument);
    
    /**
     * Desactiva sesiones de otros dispositivos
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserSession us SET us.active = false WHERE us.userDocument = :userDocument AND us.deviceId != :deviceId")
    void deactivateOtherDeviceSessions(@Param("userDocument") String userDocument, @Param("deviceId") String deviceId);
    
    /**
     * Busca por documento de usuario y dispositivo
     */
    Optional<UserSession> findByUserDocumentAndDeviceIdAndActiveTrue(String userDocument, String deviceId);
    
    /**
     * Elimina sesiones expiradas
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserSession us WHERE us.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredSessions();
}