package com.leninalbino.inventory_system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración centralizada de propiedades de la aplicación
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private final Inventory inventory = new Inventory();
    private final Mail mail = new Mail();
    
    public Inventory getInventory() {
        return inventory;
    }
    
    public Mail getMail() {
        return mail;
    }
    
    public static class Inventory {
        /**
         * Cantidad mínima de stock para considerar inventario bajo
         */
        private int lowStockThreshold = 5;
        
        public int getLowStockThreshold() {
            return lowStockThreshold;
        }
        
        public void setLowStockThreshold(int lowStockThreshold) {
            this.lowStockThreshold = lowStockThreshold;
        }
    }
    
    public static class Mail {
        private String adminEmail;
        
        public String getAdminEmail() {
            return adminEmail;
        }
        
        public void setAdminEmail(String adminEmail) {
            this.adminEmail = adminEmail;
        }
    }
}