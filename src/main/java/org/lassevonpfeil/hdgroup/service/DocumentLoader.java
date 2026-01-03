package org.lassevonpfeil.hdgroup.service;

import org.lassevonpfeil.hdgroup.model.DocumentSession;
import org.lassevonpfeil.hdgroup.model.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class DocumentLoader {

    public DocumentSession load(File file) throws IOException {

        PDDocument doc = PDDocument.load(file);
        List<Page> pages = new ArrayList<>();

        for (int i = 0; i < doc.getNumberOfPages(); i++) {
            pages.add(new Page(i));
        }

        DocumentSession session = new DocumentSession();
        session.setDocument(doc);
        session.setPages(pages);

        return session;
    }
}

