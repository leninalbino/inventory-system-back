package com.leninalbino.inventory_system.model.dto;

import lombok.Data;

@Data
public class ProductCreateRequestDto {
    private String productName;
    private String description;
    private String price; // Recibido como String desde el frontend
    private Integer quantity;
    private Long categoryId;
}

