package com.leninalbino.inventory_system.service;

import java.util.List;

public interface NotificationService {
    void notifyLowInventory(String productName, Integer quantity, List<String> emails);
}
