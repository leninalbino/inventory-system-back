package com.leninalbino.inventory_system.repository;

import com.leninalbino.inventory_system.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
