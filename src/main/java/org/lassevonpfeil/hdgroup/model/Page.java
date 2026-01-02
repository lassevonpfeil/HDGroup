package org.lassevonpfeil.hdgroup.model;

import java.awt.image.*;

public class Page {
    private final int index;
    private final BufferedImage image;

    public Page(int index, BufferedImage image) {
        this.index = index;
        this.image = image;
    }

    public int getIndex() {
        return index;
    }

    public BufferedImage getImage() {
        return image;
    }
}
