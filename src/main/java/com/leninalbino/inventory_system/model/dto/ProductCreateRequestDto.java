package com.leninalbino.inventory_system.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leninalbino.inventory_system.constants.AppConstants;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO para la creación de productos con validaciones
 */
@Data
public class ProductCreateRequestDto {
    
    @JsonProperty("name")
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = AppConstants.Product.MIN_NAME_LENGTH, max = AppConstants.Product.MAX_NAME_LENGTH, 
          message = "El nombre debe tener entre " + AppConstants.Product.MIN_NAME_LENGTH + " y " + AppConstants.Product.MAX_NAME_LENGTH + " caracteres")
    private String productName;
    
    @Size(max = AppConstants.Product.MAX_DESCRIPTION_LENGTH, 
          message = "La descripción no puede exceder " + AppConstants.Product.MAX_DESCRIPTION_LENGTH + " caracteres")
    private String description;
    
    @NotBlank(message = "El precio es obligatorio")
    @Pattern(regexp = AppConstants.Product.PRICE_REGEX, message = "El precio debe ser un número válido con máximo 2 decimales")
    private String price;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = AppConstants.Inventory.MIN_QUANTITY, message = "La cantidad no puede ser negativa")
    @Max(value = AppConstants.Inventory.MAX_QUANTITY, message = "La cantidad no puede exceder " + AppConstants.Inventory.MAX_QUANTITY)
    private Integer quantity;
    
    @JsonProperty("category")
    @NotNull(message = "La categoría es obligatoria")
    @Positive(message = "El ID de categoría debe ser positivo")
    private Long categoryId;
}
