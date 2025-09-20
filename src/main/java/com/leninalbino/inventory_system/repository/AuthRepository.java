package com.leninalbino.inventory_system.repository;

import com.leninalbino.inventory_system.model.dto.LoginResponseDto;
import com.leninalbino.inventory_system.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    User findByDocumentAndPassword(String document, String password);
    User findByDocument(String document);
    List<User> findByRolesContaining(String role);
}
