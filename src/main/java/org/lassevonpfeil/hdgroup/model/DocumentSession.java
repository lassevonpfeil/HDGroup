package org.lassevonpfeil.hdgroup.model;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.util.*;


/*public class DocumentSession {
    private List<Page> pages = new ArrayList<>();

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }
}*/
public class DocumentSession {

    private PDDocument document;
    private List<Page> pages;

    public PDDocument getDocument() {
        return document;
    }

    public void setDocument(PDDocument document) {
        this.document = document;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }
}

