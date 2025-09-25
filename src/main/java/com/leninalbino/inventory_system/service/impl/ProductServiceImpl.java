package com.leninalbino.inventory_system.service.impl;

import com.leninalbino.inventory_system.config.AppProperties;
import com.leninalbino.inventory_system.exception.BusinessException;
import com.leninalbino.inventory_system.exception.DuplicateResourceException;
import com.leninalbino.inventory_system.exception.ResourceNotFoundException;
import com.leninalbino.inventory_system.mapper.ProductMapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;
    private final WebSocketService webSocketService;
    private final AuthRepository authRepository;
    private final ProductMapper productMapper;
    private final AppProperties appProperties;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              NotificationService notificationService,
                              WebSocketService webSocketService,
                              AuthRepository authRepository,
                              ProductMapper productMapper,
                              AppProperties appProperties) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.notificationService = notificationService;
        this.webSocketService = webSocketService;
        this.authRepository = authRepository;
        this.productMapper = productMapper;
        this.appProperties = appProperties;
        logger.info("ProductService inicializado correctamente");
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        logger.info("Creando nuevo producto: {}", dto.getProductName());
        
        validateProduct(dto, null);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", dto.getCategoryId()));
        
        Product product = productMapper.toEntity(dto);
        product.setCategory(category);

        Product saved = productRepository.save(product);
        logger.info("Producto creado exitosamente con ID: {}", saved.getProductId());
        
        notifyIfLowInventory(saved);

        return productMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto dto) {
        logger.info("Actualizando producto con ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        validateProduct(dto, product);

        if (dto.getCategoryId() != null && !dto.getCategoryId().equals(product.getCategory().getCategoryId())) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría", dto.getCategoryId()));
            product.setCategory(category);
        }
        
        productMapper.updateEntityFromDto(dto, product);

        Product updated = productRepository.save(product);
        logger.info("Producto actualizado exitosamente: {}", updated.getProductName());
        
        notifyIfLowInventory(updated);

        return productMapper.toDto(updated);
    }

    private void validateProduct(ProductDto dto, Product existingProduct) {
        logger.debug("Validando producto: {}", dto.getProductName());
        
        if (dto.getQuantity() != null && dto.getQuantity() < 0) {
            throw new BusinessException("La cantidad no puede ser negativa", "INVALID_QUANTITY");
        }
        
        if (dto.getCategoryId() == null) {
            throw new BusinessException("Debe seleccionar una categoría", "CATEGORY_REQUIRED");
        }
        
        boolean nameChanged = existingProduct == null ||
                !existingProduct.getProductName().equals(dto.getProductName());
        if (nameChanged && productRepository.existsByProductName(dto.getProductName())) {
            throw new DuplicateResourceException("Producto", "nombre", dto.getProductName());
        }
        
        logger.debug("Producto validado correctamente");
    }


    private void notifyIfLowInventory(Product product) {
        int threshold = appProperties.getInventory().getLowStockThreshold();
        
        if (product.getQuantity() != null && product.getQuantity() < threshold) {
            logger.warn("Stock bajo detectado para producto '{}': {} unidades (umbral: {})", 
                       product.getProductName(), product.getQuantity(), threshold);
            
            List<String> adminEmails = authRepository.findByRolesContaining("admin")
                    .stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());
            
            try {
                notificationService.notifyLowInventory(product.getProductName(), product.getQuantity(), adminEmails);
                webSocketService.notificarInventarioBajo(product);
                logger.info("Notificaciones de stock bajo enviadas para producto '{}'", product.getProductName());
            } catch (Exception e) {
                logger.error("Error enviando notificaciones de stock bajo para producto '{}': {}", 
                           product.getProductName(), e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        logger.info("Eliminando producto con ID: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", id);
        }
        
        productRepository.deleteById(id);
        logger.info("Producto con ID {} eliminado exitosamente", id);
    }

    @Override
    public ProductDto getProduct(Long id) {
        logger.debug("Buscando producto con ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        
        return productMapper.toDto(product);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        logger.debug("Obteniendo todos los productos");
        
        List<Product> products = productRepository.findAll();
        logger.debug("Se encontraron {} productos", products.size());
        
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getLowStockProducts() {
        int threshold = appProperties.getInventory().getLowStockThreshold();
        logger.debug("Buscando productos con stock bajo (umbral: {})", threshold);
        
        List<Product> lowStockProducts = productRepository.findByQuantityLessThan(threshold);
        logger.info("Se encontraron {} productos con stock bajo", lowStockProducts.size());
        
        return lowStockProducts.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }
}
