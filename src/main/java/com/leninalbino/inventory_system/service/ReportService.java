package com.leninalbino.inventory_system.service;

import java.io.ByteArrayInputStream;

public interface ReportService {
    ByteArrayInputStream generateLowInventoryReport();
}
