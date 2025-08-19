package com.leninalbino.inventory_system.config;

import com.leninalbino.inventory_system.model.entity.Category;
import com.leninalbino.inventory_system.model.entity.Product;
import com.leninalbino.inventory_system.model.entity.User;
import com.leninalbino.inventory_system.repository.CategoryRepository;
import com.leninalbino.inventory_system.repository.ProductRepository;
import com.leninalbino.inventory_system.repository.AuthRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthRepository authRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataInitializer(AuthRepository authRepository, CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.authRepository = authRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        // Crear usuarios de ejemplo
        if (authRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setDocument("admin");
            admin.setPassword("admin"); // Cambia por hash en producción
            admin.setRoles(Collections.singleton("ROLE_ADMIN"));
            authRepository.save(admin);

            User empleado = new User();
            empleado.setUsername("empleado");
            empleado.setDocument("empleado");
            empleado.setPassword("empleado");
            empleado.setRoles(Collections.singleton("ROLE_EMPLOYEE"));
            authRepository.save(empleado);
        }

        // Crear categorías de ejemplo
        if (categoryRepository.count() == 0) {
            Category cat1 = new Category();
            cat1.setCategoryName("Electrónica");
            cat1.setDescription("Productos electrónicos");
            categoryRepository.save(cat1);

            Category cat2 = new Category();
            cat2.setCategoryName("Ropa");
            cat2.setDescription("Prendas de vestir");
            categoryRepository.save(cat2);

            // Crear productos de ejemplo
            Product prod1 = new Product();
            prod1.setProductName("Laptop");
            prod1.setDescription("Laptop Lenovo");
            prod1.setPrice(2500.0);
            prod1.setQuantity(10);
            prod1.setCategory(cat1);
            productRepository.save(prod1);

            Product prod2 = new Product();
            prod2.setProductName("Polo");
            prod2.setDescription("Polo de algodón");
            prod2.setPrice(50.0);
            prod2.setQuantity(3);
            prod2.setCategory(cat2);
            productRepository.save(prod2);
        }
    }
}
