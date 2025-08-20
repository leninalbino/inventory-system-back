package com.leninalbino.inventory_system.service.impl;

import com.leninalbino.inventory_system.model.entity.Product;
import com.leninalbino.inventory_system.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketServiceImpl implements WebSocketService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void notificarInventarioBajo(Product producto) {
        messagingTemplate.convertAndSend("/topic/inventario-bajo", producto);
    }
}
