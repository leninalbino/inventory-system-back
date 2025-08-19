package com.leninalbino.inventory_system.repository;

import com.leninalbino.inventory_system.model.dto.LoginResponseDto;
import com.leninalbino.inventory_system.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    User getUserByToken(String document, String password);
}
