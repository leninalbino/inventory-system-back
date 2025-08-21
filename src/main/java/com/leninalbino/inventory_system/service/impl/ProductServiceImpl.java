package com.leninalbino.inventory_system.service.impl;

import com.leninalbino.inventory_system.model.dto.ProductDto;
import com.leninalbino.inventory_system.model.entity.Category;
import com.leninalbino.inventory_system.model.entity.Product;
import com.leninalbino.inventory_system.model.entity.User;
import com.leninalbino.inventory_system.repository.AuthRepository;
import com.leninalbino.inventory_system.repository.CategoryRepository;
import com.leninalbino.inventory_system.repository.ProductRepository;
import com.leninalbino.inventory_system.service.NotificationService;
import com.leninalbino.inventory_system.service.ProductService;
import com.leninalbino.inventory_system.service.WebSocketService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;
    private final WebSocketService webSocketService;
    private final AuthRepository authRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              NotificationService notificationService,
                              WebSocketService webSocketService,
                              AuthRepository authRepository) {
        this.authRepository = authRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.notificationService = notificationService;
        this.webSocketService = webSocketService;
    }

    @Override
    public ProductDto createProduct(ProductDto dto) {
        validateProduct(dto, null);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        Product product = new Product();
        setProductFields(product, dto, category);

        Product saved = productRepository.save(product);
        notifyIfLowInventory(saved);

        return toDto(saved);
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        validateProduct(dto, product);

        Category category = product.getCategory();
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        }
        setProductFields(product, dto, category);

        Product updated = productRepository.save(product);
        notifyIfLowInventory(updated);

        return toDto(updated);
    }

    private void validateProduct(ProductDto dto, Product existingProduct) {
        if (dto.getQuantity() < 0) {
            throw new RuntimeException("La cantidad no puede ser negativa");
        }
        if (dto.getCategoryId() == null) {
            throw new RuntimeException("Debe seleccionar una categoría");
        }
        boolean nameChanged = existingProduct == null ||
                !existingProduct.getProductName().equals(dto.getProductName());
        if (nameChanged && productRepository.existsByProductName(dto.getProductName())) {
            throw new RuntimeException("Ya existe un producto con ese nombre");
        }
    }

    private void setProductFields(Product product, ProductDto dto, Category category) {
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setCategory(category);
    }

    private void notifyIfLowInventory(Product product) {
        if (product.getQuantity() < 5) {
            List<String> adminEmails = authRepository.findByRolesContaining("admin")
                    .stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());
            notificationService.notifyLowInventory(product.getProductName(), product.getQuantity(), adminEmails);
            webSocketService.notificarInventarioBajo(product);
        }
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
