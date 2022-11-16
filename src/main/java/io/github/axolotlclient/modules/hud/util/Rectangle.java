package io.github.axolotlclient.modules.hud.util;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

/*
 * Stores a basic rectangle.
 */

@Data
@Accessors(fluent = true)
public class Rectangle {

    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle(int x, int y, int width, int height) {
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
    }

    public Rectangle offset(DrawPosition offset) {
        return new Rectangle(x + offset.x, y + offset.y, width, height);
    }
    public Rectangle offset(int x, int y) {
        return new Rectangle(this.x + x, this.y + y, width, height);
    }

    public boolean isMouseOver(double mouseX, double mouseY){
        return mouseX>=x && mouseX<=x+width && mouseY >=y && mouseY <= y+height;
    }

    public void setData(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

}
