package com.leninalbino.inventory_system.repository;

import com.leninalbino.inventory_system.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByQuantityLessThan(Integer quantityIsLessThan);
    boolean existsByProductName(String productName);
}
