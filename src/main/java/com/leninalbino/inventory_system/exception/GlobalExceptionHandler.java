package com.leninalbino.inventory_system.exception;

import com.leninalbino.inventory_system.model.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la aplicación
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Maneja excepciones cuando un recurso no se encuentra
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.warn("Recurso no encontrado: {}", ex.getMessage());
        ApiResponse<Object> response = new ApiResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Maneja excepciones de recursos duplicados
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResource(DuplicateResourceException ex) {
        logger.warn("Recurso duplicado: {}", ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", ex.getErrorCode());
        details.put("resourceType", ex.getResourceType());
        details.put("field", ex.getFieldName());
        details.put("value", ex.getFieldValue());
        
        ApiResponse<Object> response = new ApiResponse<>(false, ex.getMessage(), details);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    /**
     * Maneja excepciones de lógica de negocio
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        logger.warn("Error de negocio: {}", ex.getMessage());
        Map<String, String> details = new HashMap<>();
        details.put("errorCode", ex.getErrorCode());
        
        ApiResponse<Object> response = new ApiResponse<>(false, ex.getMessage(), details);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Maneja errores de validación en los DTOs
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        logger.warn("Error de validación en los datos de entrada");
        
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        
        ApiResponse<Object> response = new ApiResponse<>(false, "Error de validación en los datos", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Maneja errores de autenticación
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        logger.warn("Intento de autenticación fallido: {}", ex.getMessage());
        ApiResponse<Object> response = new ApiResponse<>(false, "Credenciales inválidas", null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Maneja errores de autorización
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Acceso denegado: {}", ex.getMessage());
        ApiResponse<Object> response = new ApiResponse<>(false, "No tiene permisos para realizar esta acción", null);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Maneja todas las demás excepciones no específicamente manejadas
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        logger.error("Error inesperado: {}", ex.getMessage(), ex);
        ApiResponse<Object> response = new ApiResponse<>(false, "Error interno del servidor", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Maneja excepciones generales
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        logger.error("Error inesperado no manejado: {}", ex.getMessage(), ex);
        ApiResponse<Object> response = new ApiResponse<>(false, "Error interno del servidor", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}