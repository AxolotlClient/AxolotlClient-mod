package io.github.axolotlclient.modules.hud.gui.component;

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
