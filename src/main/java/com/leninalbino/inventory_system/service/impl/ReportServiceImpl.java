package com.leninalbino.inventory_system.service.impl;

import com.leninalbino.inventory_system.model.entity.Product;
import com.leninalbino.inventory_system.repository.ProductRepository;
import com.leninalbino.inventory_system.service.ReportService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public ByteArrayInputStream generateLowInventoryReport() {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Reporte de Productos con Inventario Bajo"));
            document.add(new Paragraph(" "));

            List<Product> lowStock = productRepository.findByQuantityLessThan(5);

            for (Product product : lowStock) {
                document.add(new Paragraph(product.getProductName() + " - Cantidad: " + product.getQuantity()));
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
