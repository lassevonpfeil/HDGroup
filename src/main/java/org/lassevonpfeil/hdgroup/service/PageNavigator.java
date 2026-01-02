package org.lassevonpfeil.hdgroup.service;

import org.lassevonpfeil.hdgroup.model.Page;

import java.util.*;

public class PageNavigator {
    private final List<Page> pages;
    private int index = 0;

    public PageNavigator(List<Page> pages) {
        this.pages = pages;
    }

    public Page current() {
        return pages.get(index);
    }

    public boolean next() {
        if (index < pages.size() - 1) {
            index++;
            return true;
        }
        return false;
    }

    public boolean previous() {
        if (index > 0) {
            index--;
            return true;
        }
        return false;
    }

    public int getIndex() {
        return index;
    }

    public int total() {
        return pages.size();
    }
}
