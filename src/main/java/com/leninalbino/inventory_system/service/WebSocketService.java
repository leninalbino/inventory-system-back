package com.leninalbino.inventory_system.service;

import com.leninalbino.inventory_system.model.entity.Product;

public interface WebSocketService {
    void notificarInventarioBajo(Product producto);
}
