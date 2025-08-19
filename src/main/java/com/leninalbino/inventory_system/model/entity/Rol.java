package com.leninalbino.inventory_system.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String rolName;
}
