package com.leninalbino.inventory_system.service.impl;

import com.leninalbino.inventory_system.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final JavaMailSender mailSender;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${admin.email}")
    private String adminEmail;

    public NotificationServiceImpl(JavaMailSender mailSender, SimpMessagingTemplate messagingTemplate) {
        this.mailSender = mailSender;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void notifyLowInventory(String productName, Integer quantity) {
        // Notificación por correo
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject("Inventario bajo: " + productName);
        message.setText("El producto '" + productName + "' tiene solo " + quantity + " unidades en inventario.");
        mailSender.send(message);

        // Notificación por WebSocket
        messagingTemplate.convertAndSend("/topic/low-inventory",
                "El producto '" + productName + "' tiene solo " + quantity + " unidades en inventario.");
    }
}
