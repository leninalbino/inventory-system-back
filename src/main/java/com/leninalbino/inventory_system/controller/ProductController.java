package com.leninalbino.inventory_system.controller;

import com.leninalbino.inventory_system.model.dto.ApiResponse;
import com.leninalbino.inventory_system.model.dto.ProductCreateRequestDto;
import com.leninalbino.inventory_system.model.dto.ProductDto;
import com.leninalbino.inventory_system.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductDto> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProduct(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDto>> create(@RequestBody @Valid ProductCreateRequestDto requestDto) {
        try {
            // Conversión segura de String a Double
            Double price;
            try {
                price = Double.parseDouble(requestDto.getPrice());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "El precio debe ser un número válido.", null));
            }
            ProductDto productDto = new ProductDto();
            productDto.setProductName(requestDto.getProductName());
            productDto.setDescription(requestDto.getDescription());
            productDto.setPrice(price);
            productDto.setQuantity(requestDto.getQuantity());
            productDto.setCategoryId(requestDto.getCategoryId());
            ProductDto saved = productService.createProduct(productDto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Producto creado exitosamente", saved));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid ProductDto productDto) {
        try {
            ProductDto updated = productService.updateProduct(id, productDto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Producto actualizado exitosamente", updated));
        } catch (RuntimeException e) {
            if ("Producto no encontrado".equals(e.getMessage())) {
                return ResponseEntity.status(404).body(new ApiResponse<>(false, e.getMessage(), null));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Producto eliminado exitosamente", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, "Producto no encontrado", null));
        }
    }

    @GetMapping("/low-inventory")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getLowInventory() {
        List<ProductDto> products = productService.getLowStockProducts();
        return ResponseEntity.ok(new ApiResponse<>(true, "Productos con inventario bajo", products));
    }
}