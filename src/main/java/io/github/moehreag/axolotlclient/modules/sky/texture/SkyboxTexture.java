package io.github.moehreag.axolotlclient.modules.sky.texture;

import io.github.moehreag.axolotlclient.Axolotlclient;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class SkyboxTexture extends ResourceTexture {

    public Orientation side;
    public int height;
    public int width;

    public SkyboxTexture(Identifier identifier, int orientation) {
        super(identifier);
        switch (orientation){
            case 1: side=Orientation.NORTH; break;
            case 2: side=Orientation.SOUTH; break;
            case 3: side=Orientation.TOP; break;
            case 4: side=Orientation.EAST; break;
            case 5: side=Orientation.WEST; break;
            default: side=Orientation.BOTTOM; break;
        }
    }

    public BufferedImage rotateToOrientation(@NotNull BufferedImage image){

        int widthOfImage = image.getWidth();
        int heightOfImage = image.getHeight();
        int typeOfImage = image.getType();

        BufferedImage rotated = new BufferedImage(widthOfImage, heightOfImage, typeOfImage);

        Graphics2D g2D = rotated.createGraphics();



        switch (this.side){
            case TOP:
            case WEST:
                g2D.rotate(Math.toRadians(270), widthOfImage / (float)2, heightOfImage / (float)2);
                break;
            case EAST:
                g2D.rotate(Math.toRadians(90), widthOfImage / (float)2, heightOfImage / (float)2);
                break;
            case SOUTH:
                g2D.rotate(Math.toRadians(180), widthOfImage / (float)2, heightOfImage / (float)2);
                break;
            default:
                return image;
        }
        g2D.drawImage(image, null, 0, 0);
        return rotated;
    }

    @Override
    public void load(@NotNull ResourceManager manager) throws IOException {
        this.clearGlId();
        InputStream inputStream = null;

        try {
            Resource resource = manager.getResource(this.field_6555);
            inputStream = resource.getInputStream();
            BufferedImage bufferedImage;
            if(side!=null)bufferedImage = rotateToOrientation(TextureUtil.create(inputStream));
            else bufferedImage = TextureUtil.create(inputStream);
            width=bufferedImage.getWidth();
            height=bufferedImage.getHeight();

            boolean bl = false;
            boolean bl2 = false;
            if (resource.hasMetadata()) {
                try {
                    TextureResourceMetadata textureResourceMetadata = resource.getMetadata("texture");
                    if (textureResourceMetadata != null) {
                        bl = textureResourceMetadata.method_5980();
                        bl2 = textureResourceMetadata.method_5981();
                    }
                } catch (RuntimeException var11) {
                    Axolotlclient.LOGGER.warn("Failed reading metadata of: " + this.field_6555, var11);
                }
            }

            TextureUtil.method_5860(this.getGlId(), bufferedImage, bl, bl2);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }

        }

    }

    public int getHeight(){
        return height;
    }

    public int getWidth() {
        return width;
    }

    public enum Orientation {
        BOTTOM,
        NORTH,
        SOUTH,
        TOP,
        WEST,
        EAST
    }
}
