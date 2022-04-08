package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.config.options.ColorOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.moehreag.axolotlclient.modules.hud.util.Color;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawPosition;
import io.github.moehreag.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class KeystrokeHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "keystrokehud");

    private final ColorOption pressedTextColor = new ColorOption("heldtextcolor", Color.parse("#FF000000"));
    private final ColorOption pressedBackgroundColor = new ColorOption( "heldbackgroundcolor", Color.parse("#64FFFFFF"));
    private ArrayList<Keystroke> keystrokes;
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public KeystrokeHud() {
        super(53, 61);
    }

    public static Optional<String> getMouseKeyBindName(KeyBinding keyBinding) {
        if (keyBinding.getTranslationKey().equalsIgnoreCase(client.options.keyAttack.getTranslationKey())) {
            return Optional.of("LMB");
        } else if (keyBinding.getTranslationKey().equalsIgnoreCase(client.options.keyUse.getTranslationKey())) {
            return Optional.of("RMB");
        } else if (keyBinding.getTranslationKey().equalsIgnoreCase(client.options.keyPickItem.getTranslationKey())) {
            return Optional.of("MMB");
        }
        return Optional.empty();
    }

    public void setKeystrokes() {
        keystrokes = new ArrayList<>();
        DrawPosition pos = getPos();
        // LMB
        keystrokes.add(createFromKey(new Rectangle(0, 36, 26, 17), pos, client.options.keyAttack));
        // RMB
        keystrokes.add(createFromKey(new Rectangle(27, 36, 26, 17), pos, client.options.keyUse));
        // W
        keystrokes.add(createFromKey(new Rectangle(18, 0, 17, 17), pos, client.options.keyForward));
        // A
        keystrokes.add(createFromKey(new Rectangle(0, 18, 17, 17), pos, client.options.keyLeft));
        // S
        keystrokes.add(createFromKey(new Rectangle(18, 18, 17, 17), pos, client.options.keyBack));
        // D
        keystrokes.add(createFromKey(new Rectangle(36, 18, 17, 17), pos, client.options.keyRight));

        // Space
        keystrokes.add(new Keystroke(new Rectangle(0, 54, 53, 7), pos, client.options.keyJump, (stroke) -> {
            Rectangle bounds = stroke.bounds;
            Rectangle spaceBounds = new Rectangle(bounds.x + stroke.offset.x + 4,
                    bounds.y + stroke.offset.y + 2,
                    bounds.width - 8, 1);
            fillRect(spaceBounds, stroke.getFGColor());
            if (shadow.get()) {
                fillRect(spaceBounds.offset(1, 1),
                        new Color((stroke.getFGColor().getAsInt() & 16579836) >> 2 | stroke.getFGColor().getAsInt() & -16777216));
            }
        }));
        KeyBinding.unpressAll();
        KeyBinding.updateKeysByCode();
    }

    @Override
    public void render() {
        scale();
        if (keystrokes == null) {
            setKeystrokes();
        }
        for (Keystroke stroke : keystrokes) {
            stroke.render();
        }
        GlStateManager.popMatrix();
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        DrawPosition pos = getPos();
        if (keystrokes == null) {
            setKeystrokes();
        }
        for (Keystroke stroke : keystrokes) {
            stroke.offset = pos;
        }
    }

    @Override
    protected boolean getShadowDefault() {
        return false;
    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        if (keystrokes == null) {
            setKeystrokes();
        }
        for (Keystroke stroke : keystrokes) {
            stroke.render();
        }
        GlStateManager.popMatrix();
        hovered = false;
    }

    public Keystroke createFromKey(Rectangle bounds, DrawPosition offset, KeyBinding key) {
        String name = getMouseKeyBindName(key).orElse(GameOptions.getFormattedNameForKeyCode(key.getCode()).toUpperCase());
        if (name.length() > 4) {
            name = name.substring(0, 2);
        }
        return createFromString(bounds, offset, key, name);
    }

    public Keystroke createFromString(Rectangle bounds, DrawPosition offset, KeyBinding key, String word) {
        return new Keystroke(bounds, offset, key, (stroke) -> {
            Rectangle strokeBounds = stroke.bounds;
            float x = (strokeBounds.x + stroke.offset.x + ((float) strokeBounds.width / 2)) -
                    ((float) client.textRenderer.getStringWidth(word) / 2 -1);
            float y = strokeBounds.y + stroke.offset.y + ((float) strokeBounds.height / 2) - 4;

            drawString(client.textRenderer, word, (int) x, (int) y, stroke.getFGColor().getAsInt(), shadow.get());
        });
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        options.add(textColor);
        options.add(pressedTextColor);
        options.add(shadow);
        options.add(background);
        options.add(backgroundColor);
        options.add(pressedBackgroundColor);
    }

    public class Keystroke {
        public final KeyBinding key;
        public final KeystrokeRenderer render;
        public Rectangle bounds;
        public DrawPosition offset;
        private float start = -1;
        private final int animTime = 100;
        private boolean wasPressed = true;

        public Keystroke(Rectangle bounds, DrawPosition offset, KeyBinding key, KeystrokeRenderer render) {
            this.bounds = bounds;
            this.offset = offset;
            this.key = key;
            this.render = render;
        }

        public void setPos(int x, int y) {
            bounds = new Rectangle(x, y, bounds.width, bounds.height);
        }

        public void setDimensions(int width, int height) {
            bounds = new Rectangle(bounds.x, bounds.y, width, height);
        }

        public void setBounds(int x, int y, int width, int height) {
            bounds = new Rectangle(x, y, width, height);
        }

        public void renderStroke() {
            if (key.isPressed() != wasPressed) {
                start = System.nanoTime() / 1000000F;

            }
            if (background.get()) {
                fillRect(bounds.offset(offset),
                        getColor());
            }
            if ((System.nanoTime() / 1000000F - start) / animTime >= 1) {
                start = -1;
            }
            wasPressed = key.isPressed();
        }

        private float getPercentPressed() {
            return start == -1 ? 1 : MathHelper.clamp((System.nanoTime() / 1000000F - start) / animTime, 0, 1);
        }

        public Color getColor() {
            return key.isPressed() ? Color.blend(backgroundColor.get(), pressedBackgroundColor.get(),
                    getPercentPressed()) :
                    Color.blend(pressedBackgroundColor.get(),
                    backgroundColor.get(),
                    getPercentPressed());
        }

        public Color getFGColor() {
            return key.isPressed() ? Color.blend(textColor.get(), pressedTextColor.get(), getPercentPressed()) :
                    Color.blend(pressedTextColor.get(),
                            textColor.get(),
                            getPercentPressed());
        }

        public void render() {
            renderStroke();
            render.render(this);
        }

    }

    public interface KeystrokeRenderer {
        void render(Keystroke stroke);
    }

}