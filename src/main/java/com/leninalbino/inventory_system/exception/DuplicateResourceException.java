package com.leninalbino.inventory_system.exception;

/**
 * Excepción lanzada cuando se intenta crear un recurso que ya existe
 */
public class DuplicateResourceException extends BusinessException {
    
    private final String resourceType;
    private final String fieldName;
    private final Object fieldValue;
    
    public DuplicateResourceException(String resourceType, String fieldName, Object fieldValue) {
        super(String.format("%s con %s '%s' ya existe", resourceType, fieldName, fieldValue), 
              "DUPLICATE_RESOURCE");
        this.resourceType = resourceType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getFieldValue() {
        return fieldValue;
    }
}