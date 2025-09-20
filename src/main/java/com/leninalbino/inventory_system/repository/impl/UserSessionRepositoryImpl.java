package com.leninalbino.inventory_system.repository.impl;

import com.leninalbino.inventory_system.model.entity.UserSession;
import com.leninalbino.inventory_system.repository.UserSessionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserSessionRepositoryImpl implements UserSessionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserSession save(UserSession session) {
        if (session.getId() == null) {
            entityManager.persist(session);
            return session;
        } else {
            return entityManager.merge(session);
        }
    }

    @Override
    public Optional<UserSession> findByRefreshToken(String refreshToken) {
        try {
            TypedQuery<UserSession> query = entityManager.createQuery(
                "SELECT s FROM UserSession s WHERE s.refreshToken = :refreshToken AND s.active = true AND s.expiresAt > :now",
                UserSession.class
            );
            query.setParameter("refreshToken", refreshToken);
            query.setParameter("now", LocalDateTime.now());
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<UserSession> findActiveSessionsByUserDocument(String userDocument) {
        TypedQuery<UserSession> query = entityManager.createQuery(
            "SELECT s FROM UserSession s WHERE s.userDocument = :userDocument AND s.active = true AND s.expiresAt > :now",
            UserSession.class
        );
        query.setParameter("userDocument", userDocument);
        query.setParameter("now", LocalDateTime.now());
        return query.getResultList();
    }

    @Override
    public void deleteByRefreshToken(String refreshToken) {
        entityManager.createQuery("DELETE FROM UserSession s WHERE s.refreshToken = :refreshToken")
                .setParameter("refreshToken", refreshToken)
                .executeUpdate();
    }

    @Override
    public void deactivateAllUserSessions(String userDocument) {
        entityManager.createQuery("UPDATE UserSession s SET s.active = false WHERE s.userDocument = :userDocument")
                .setParameter("userDocument", userDocument)
                .executeUpdate();
    }

    @Override
    public void deactivateOtherDeviceSessions(String userDocument, String deviceId) {
        entityManager.createQuery("UPDATE UserSession s SET s.active = false WHERE s.userDocument = :userDocument AND s.deviceId != :deviceId")
                .setParameter("userDocument", userDocument)
                .setParameter("deviceId", deviceId)
                .executeUpdate();
    }
}