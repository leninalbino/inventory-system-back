package com.leninalbino.inventory_system.mapper;

import com.leninalbino.inventory_system.model.dto.ProductCreateRequestDto;
import com.leninalbino.inventory_system.model.dto.ProductDto;
import com.leninalbino.inventory_system.model.entity.Product;
import org.mapstruct.*;

/**
 * Mapper para convertir entre entidades Product y DTOs usando MapStruct
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    
    /**
     * Convierte una entidad Product a ProductDto
     * @param product la entidad a convertir
     * @return el DTO correspondiente
     */
    @Mapping(source = "category.categoryId", target = "categoryId")
    ProductDto toDto(Product product);
    
    /**
     * Convierte un ProductDto a entidad Product
     * @param dto el DTO a convertir
     * @return la entidad correspondiente
     */
    @Mapping(source = "categoryId", target = "category.categoryId")
    Product toEntity(ProductDto dto);
    
    /**
     * Convierte un ProductCreateRequestDto a ProductDto
     * @param createRequest el DTO de request a convertir
     * @return el DTO correspondiente
     */
    @Mapping(target = "productId", ignore = true)
    @Mapping(source = "price", target = "price", qualifiedByName = "stringToDouble")
    ProductDto createRequestToDto(ProductCreateRequestDto createRequest);
    
    /**
     * Actualiza una entidad Product existente con datos de un ProductDto
     * @param dto los nuevos datos
     * @param product la entidad a actualizar
     */
    @Mapping(source = "categoryId", target = "category.categoryId")
    @Mapping(target = "productId", ignore = true)
    void updateEntityFromDto(ProductDto dto, @MappingTarget Product product);
    
    /**
     * Convierte String a Double para el precio
     * @param price precio como String
     * @return precio como Double
     */
    @Named("stringToDouble")
    default Double stringToDouble(String price) {
        if (price == null || price.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(price);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato de precio inválido: " + price);
        }
    }
}