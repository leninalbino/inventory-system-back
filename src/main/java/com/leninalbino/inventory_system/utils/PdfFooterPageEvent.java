package com.leninalbino.inventory_system.utils;

import com.lowagie.text.*;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.*;

public class PdfFooterPageEvent extends PdfPageEventHelper {
    private Font footerFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        Phrase footer = new Phrase("Página " + writer.getPageNumber(), footerFont);
        float x = (document.right() + document.left()) / 2;
        float y = document.bottom() - 10;
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, x, y, 0);
    }
}
