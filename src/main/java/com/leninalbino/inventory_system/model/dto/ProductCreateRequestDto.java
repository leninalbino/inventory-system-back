package com.leninalbino.inventory_system.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductCreateRequestDto {
    @JsonProperty("name")
    private String productName;
    private String description;
    private String price;
    private Integer quantity;
    @JsonProperty("category")
    private Long categoryId;
}
