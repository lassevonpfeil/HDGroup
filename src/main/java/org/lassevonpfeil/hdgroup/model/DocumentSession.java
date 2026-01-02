package org.lassevonpfeil.hdgroup.model;

import java.util.*;


public class DocumentSession {
    private List<Page> pages = new ArrayList<>();

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }
}
