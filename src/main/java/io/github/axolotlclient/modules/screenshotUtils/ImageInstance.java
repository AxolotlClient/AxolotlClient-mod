package io.github.axolotlclient.modules.screenshotUtils;

import java.awt.image.BufferedImage;

public class ImageInstance {

    private final BufferedImage image;
    private final String fileName;
    public ImageInstance(BufferedImage image, String name){
        this.image = image;
        this.fileName = name;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getFileName() {
        return fileName;
    }
}
