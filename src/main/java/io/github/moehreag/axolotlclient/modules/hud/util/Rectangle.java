package io.github.moehreag.axolotlclient.modules.hud.util;
/*
 * Stores a basic rectangle.
 */

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

}
