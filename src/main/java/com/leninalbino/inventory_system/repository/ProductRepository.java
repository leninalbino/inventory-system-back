package com.leninalbino.inventory_system.repository;

import com.leninalbino.inventory_system.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
