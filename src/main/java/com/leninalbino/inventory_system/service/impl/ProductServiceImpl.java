package com.leninalbino.inventory_system.service.impl;

import com.leninalbino.inventory_system.model.dto.ProductDto;
import com.leninalbino.inventory_system.model.entity.Category;
import com.leninalbino.inventory_system.model.entity.Product;
import com.leninalbino.inventory_system.repository.CategoryRepository;
import com.leninalbino.inventory_system.repository.ProductRepository;
import com.leninalbino.inventory_system.service.NotificationService;
import com.leninalbino.inventory_system.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              NotificationService notificationService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.notificationService = notificationService;
    }

    @Override
    public ProductDto createProduct(ProductDto dto) {
        if (dto.getQuantity() < 0) {
            throw new RuntimeException("La cantidad no puede ser negativa");
        }
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setCategory(category);
        Product saved = productRepository.save(product);

        if (saved.getQuantity() != null && saved.getQuantity() < 5) {
            notificationService.notifyLowInventory(saved.getProductName(), saved.getQuantity());
        }

        return toDto(saved);
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        if (dto.getQuantity() < 0) {
            throw new RuntimeException("La cantidad no puede ser negativa");
        }
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            product.setCategory(category);
        }
        Product updated = productRepository.save(product);

        if (updated.getQuantity() != null && updated.getQuantity() < 5) {
            notificationService.notifyLowInventory(updated.getProductName(), updated.getQuantity());
        }

        return toDto(updated);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public ProductDto getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return toDto(product);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getLowStockProducts() {
        return productRepository.findAll().stream()
                .filter(p -> p.getQuantity() != null && p.getQuantity() < 5)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null);
        return dto;
    }
}
