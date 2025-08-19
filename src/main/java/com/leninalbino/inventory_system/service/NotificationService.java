package com.leninalbino.inventory_system.service;

public interface NotificationService {
    void notifyLowInventory(String productName, Integer quantity);
}
