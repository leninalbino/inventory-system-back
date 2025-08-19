package com.leninalbino.inventory_system.service;

import com.leninalbino.inventory_system.model.dto.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto dto);
    ProductDto updateProduct(Long id, ProductDto dto);
    void deleteProduct(Long id);
    ProductDto getProduct(Long id);
    List<ProductDto> getAllProducts();
    List<ProductDto> getLowStockProducts();
}
