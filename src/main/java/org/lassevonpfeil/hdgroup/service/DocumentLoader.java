package org.lassevonpfeil.hdgroup.service;

import org.lassevonpfeil.hdgroup.model.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class DocumentLoader {
    public List<Page> load(File file) throws IOException {
        if (file.getName().toLowerCase().endsWith(".pdf")) {
            return loadPdf(file);
        }
        throw new UnsupportedOperationException("Noch nicht implementiert");
    }

    private List<Page> loadPdf(File file) throws IOException {
        List<Page> pages = new ArrayList<>();

        try (PDDocument doc = PDDocument.load(file)) {
            PDFRenderer renderer = new PDFRenderer(doc);

            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                BufferedImage img = renderer.renderImageWithDPI(i, 300);
                pages.add(new Page(i, img));
            }
        }
        return pages;
    }


}
