package com.leninalbino.inventory_system.constants;

/**
 * Constantes de la aplicación para evitar números mágicos y valores hardcodeados
 */
public final class AppConstants {
    
    // Constructor privado para prevenir instanciación
    private AppConstants() {
        throw new UnsupportedOperationException("Esta es una clase de utilidades");
    }
    
    /**
     * Constantes relacionadas con el inventario
     */
    public static final class Inventory {
        public static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;
        public static final int MIN_QUANTITY = 0;
        public static final int MAX_QUANTITY = 999999;
        
        private Inventory() {}
    }
    
    /**
     * Constantes relacionadas con usuarios
     */
    public static final class User {
        public static final int MIN_USERNAME_LENGTH = 3;
        public static final int MAX_USERNAME_LENGTH = 50;
        public static final int MIN_PASSWORD_LENGTH = 6;
        public static final int MAX_PASSWORD_LENGTH = 100;
        public static final int MIN_DOCUMENT_LENGTH = 8;
        public static final int MAX_DOCUMENT_LENGTH = 15;
        public static final int MAX_EMAIL_LENGTH = 100;
        
        private User() {}
    }
    
    /**
     * Constantes relacionadas con productos
     */
    public static final class Product {
        public static final int MIN_NAME_LENGTH = 2;
        public static final int MAX_NAME_LENGTH = 100;
        public static final int MAX_DESCRIPTION_LENGTH = 500;
        public static final String PRICE_REGEX = "^\\d+(\\.\\d{1,2})?$";
        
        private Product() {}
    }
    
    /**
     * Constantes de seguridad
     */
    public static final class Security {
        public static final String ROLE_PREFIX = "ROLE_";
        public static final String ADMIN_ROLE = "ROLE_ADMIN";
        public static final String EMPLOYEE_ROLE = "ROLE_EMPLOYEE";
        public static final String BEARER_PREFIX = "Bearer ";
        public static final String AUTHORIZATION_HEADER = "Authorization";
        
        private Security() {}
    }
    
    /**
     * Constantes de regex para validaciones
     */
    public static final class Regex {
        public static final String DOCUMENT_PATTERN = "^[0-9]{8,15}$";
        public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]+$";
        public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
        
        private Regex() {}
    }
    
    /**
     * Constantes de mensajes
     */
    public static final class Messages {
        public static final String RESOURCE_NOT_FOUND = "%s con ID '%s' no encontrado";
        public static final String DUPLICATE_RESOURCE = "%s con %s '%s' ya existe";
        public static final String VALIDATION_ERROR = "Error de validación en los datos";
        public static final String ACCESS_DENIED = "No tiene permisos para realizar esta acción";
        public static final String INVALID_CREDENTIALS = "Credenciales inválidas";
        
        private Messages() {}
    }
}