package io.github.axolotlclient.modules.hud.util;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class DrawPosition {

    public int x;
    public int y;

    public DrawPosition(int x, int y) {
        this.x=x;
        this.y=y;
    }

    public DrawPosition subtract(int x, int y) {
        this.x-=x;
        this.y-=y;
        return this;
    }

    public DrawPosition copy(){
        return new DrawPosition(x, y);
    }

    public DrawPosition divide(float scale) {
        x/=scale;
        y/=scale;
        return this;
    }

}
