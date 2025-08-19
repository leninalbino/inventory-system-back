package com.leninalbino.inventory_system.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "Users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    private String userName;
    private String password;
    private String document;
    private String typeDocument;

    //private Set<Rol> roles;
}
