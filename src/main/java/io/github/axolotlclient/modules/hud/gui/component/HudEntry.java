package io.github.axolotlclient.modules.hud.gui.component;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public interface HudEntry extends Identifiable, Configurable, Positionable {

    boolean isEnabled();

    void setEnabled(boolean value);

    default boolean tickable() {
        return false;
    }

    default boolean overridesF3() {
        return false;
    }

    default void tick() {
    }

    default void init() {
    }

    default double getDefaultX() {
        return 0;
    }

    default double getDefaultY() {
        return 0;
    }

    void render(float delta);

    void renderPlaceholder(float delta);

    void setHovered(boolean hovered);
}
