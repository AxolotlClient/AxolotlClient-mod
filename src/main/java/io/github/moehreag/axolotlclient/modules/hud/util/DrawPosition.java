package io.github.moehreag.axolotlclient.modules.hud.util;


public class DrawPosition {

    public int x;
    public int y;

    public DrawPosition(int x, int y) {
        this.x=x;
        this.y=y;
    }

    public DrawPosition subtract(int x, int y) {
        return new DrawPosition(this.x - x, this.y - y);
    }

    public DrawPosition subtract(DrawPosition position) {
        return new DrawPosition(position.x, position.y);
    }

    public DrawPosition divide(float scale) {
        return new DrawPosition((int) (x / scale), (int) (y / scale));
    }

}
