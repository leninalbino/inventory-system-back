package com.leninalbino.inventory_system.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String productName;
    private String description;
    private Double price;
    private Integer quantity;
    //private Set(Category) category;
}
