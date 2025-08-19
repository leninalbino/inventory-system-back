package com.leninalbino.inventory_system.model.dto;

import lombok.Data;

@Data
public class ProductDto {
    private Long productId;
    private String productName;
    private String description;
    private Double price;
    private Integer quantity;
    private Long categoryId;
}
