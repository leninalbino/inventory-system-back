package com.leninalbino.inventory_system.model.dto;

import com.leninalbino.inventory_system.constants.AppConstants;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

/**
 * DTO para el registro de usuarios con validaciones
 */
@Data
public class RegisterRequestDto {
    
    @NotBlank(message = "El documento es obligatorio")
    @Pattern(regexp = AppConstants.Regex.DOCUMENT_PATTERN, 
             message = "El documento debe contener entre " + AppConstants.User.MIN_DOCUMENT_LENGTH + " y " + AppConstants.User.MAX_DOCUMENT_LENGTH + " dígitos")
    private String document;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = AppConstants.User.MIN_PASSWORD_LENGTH, max = AppConstants.User.MAX_PASSWORD_LENGTH, 
          message = "La contraseña debe tener entre " + AppConstants.User.MIN_PASSWORD_LENGTH + " y " + AppConstants.User.MAX_PASSWORD_LENGTH + " caracteres")
    @Pattern(regexp = AppConstants.Regex.PASSWORD_PATTERN, 
             message = "La contraseña debe contener al menos una letra minúscula, una mayúscula y un número")
    private String password;
    
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = AppConstants.User.MIN_USERNAME_LENGTH, max = AppConstants.User.MAX_USERNAME_LENGTH, 
          message = "El nombre de usuario debe tener entre " + AppConstants.User.MIN_USERNAME_LENGTH + " y " + AppConstants.User.MAX_USERNAME_LENGTH + " caracteres")
    @Pattern(regexp = AppConstants.Regex.USERNAME_PATTERN, 
             message = "El nombre de usuario solo puede contener letras, números y guiones bajos")
    private String username;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = AppConstants.User.MAX_EMAIL_LENGTH, 
          message = "El email no puede exceder " + AppConstants.User.MAX_EMAIL_LENGTH + " caracteres")
    private String email;
    
    @NotEmpty(message = "Debe especificar al menos un rol")
    @Size(min = 1, max = 3, message = "Debe tener entre 1 y 3 roles")
    private Set<String> roles;
}
