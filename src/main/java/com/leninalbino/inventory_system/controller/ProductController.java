package com.leninalbino.inventory_system.controller;

import com.leninalbino.inventory_system.model.dto.ApiResponse;
import com.leninalbino.inventory_system.model.dto.ProductCreateRequestDto;
import com.leninalbino.inventory_system.model.dto.ProductDto;
import com.leninalbino.inventory_system.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "API para gestión de productos en el inventario")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los productos", description = "Devuelve una lista con todos los productos del inventario")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class)))
    })
    public List<ProductDto> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Devuelve un producto específico según su ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductDto> getById(
            @Parameter(description = "ID del producto a buscar", required = true) 
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProduct(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear nuevo producto", description = "Crea un nuevo producto en el inventario")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ApiResponse<ProductDto>> create(
            @Parameter(description = "Datos del producto a crear", required = true)
            @RequestBody @Valid ProductCreateRequestDto requestDto) {
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
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid ProductCreateRequestDto requestDto) {
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