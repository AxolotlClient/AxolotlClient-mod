package io.github.axolotlclient.modules.screenshotUtils;

import com.mojang.blaze3d.texture.NativeImage;

public class ImageInstance {

    private final NativeImage image;
    private final String fileName;
    public ImageInstance(NativeImage image, String name){
        this.image = image;
        this.fileName = name;
    }

    public NativeImage getImage() {
        return image;
    }

    public String getFileName() {
        return fileName;
    }
}
