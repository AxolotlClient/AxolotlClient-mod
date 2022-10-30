package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import io.github.axolotlclient.util.Hooks;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KeystrokeHud extends TextHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "keystrokehud");

    private final ColorOption pressedTextColor = new ColorOption("axolotlclient.heldtextcolor", new Color(0xFF000000));
    private final ColorOption pressedBackgroundColor = new ColorOption("axolotlclient.heldbackgroundcolor", 0x64FFFFFF);
    private final ColorOption pressedOutlineColor = new ColorOption("axolotlclient.heldoutlinecolor",Color.BLACK);
    private final BooleanOption mouseMovement = new BooleanOption("axolotlclient.mousemovement", this::onMouseMovementOption, false);
    private ArrayList<Keystroke> keystrokes;
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private float mouseX = 0;
    private float mouseY = 0;
    private float lastMouseX = 0;
    private float lastMouseY = 0;

    public KeystrokeHud() {
        super(53, 61, true);
        Hooks.KEYBIND_CHANGE.register(key -> setKeystrokes());
        Hooks.PLAYER_DIRECTION_CHANGE.register(this::onPlayerDirectionChange);
    }

    public static Optional<String> getMouseKeyBindName(KeyBinding keyBinding) {
        if (keyBinding.getTranslationKey()
                      .equalsIgnoreCase(client.options.keyAttack.getTranslationKey())) {
            return Optional.of("LMB");
        } else if (keyBinding.getTranslationKey()
                             .equalsIgnoreCase(client.options.keyUse.getTranslationKey())) {
            return Optional.of("RMB");
        } else if (keyBinding.getTranslationKey()
                             .equalsIgnoreCase(client.options.keyPickItem.getTranslationKey())) {
            return Optional.of("MMB");
        }
        return Optional.empty();
    }

    public void setKeystrokes() {
        if (Util.getWindow() == null) {
            keystrokes = null;
            return;
            // Wait until render is called
        }
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
            Rectangle spaceBounds = new Rectangle(bounds.x() + stroke.offset.x() + 4,
                    bounds.y() + stroke.offset.y() + 2,
                    bounds.width() - 8, 1
            );
            fillRect(spaceBounds, stroke.getFGColor());
            if (shadow.get()) {
                fillRect(spaceBounds.offset(1, 1),
                        new Color((stroke.getFGColor().getAsInt() & 16579836) >> 2 | stroke.getFGColor().getAsInt() & -16777216)
                );
            }
        }));
        KeyBinding.unpressAll();
        KeyBinding.updateKeysByCode();
    }

    @Override
    public void render(float delta) {
        GlStateManager.pushMatrix();
        scale();
        renderComponent(delta);
        GlStateManager.popMatrix();
    }

    @Override
    public void renderComponent(float delta) {
        if (keystrokes == null) {
            setKeystrokes();
        }
        for (Keystroke stroke : keystrokes) {
            stroke.render();
        }
        if (mouseMovement.get()) {
            int spaceY = 62 + getRawY();
            int spaceX = getRawX();
            if (background.get()) {
                DrawUtil.fillRect(spaceX, spaceY, width, 35, backgroundColor.get().getAsInt());
            }
            if (outline.get()) {
                DrawUtil.outlineRect(spaceX, spaceY, width, 35, outlineColor.get().getAsInt());
            }

            float calculatedMouseX = (lastMouseX + ((mouseX - lastMouseX) * delta)) - 5;
            float calculatedMouseY = (lastMouseY + ((mouseY - lastMouseY) * delta)) - 5;

            DrawUtil.fillRect(spaceX + (width / 2) - 1, spaceY + 17, 1, 1, Color.WHITE.getAsInt());

            GlStateManager.translatef(calculatedMouseX, calculatedMouseY, 0); // Woah KodeToad, good use of translate

            DrawUtil.outlineRect(
                    spaceX + (width / 2) - 1,
                    spaceY + 17,
                    11,
                    11,
                    Color.WHITE.getAsInt()
            );
        }
    }

    public void onPlayerDirectionChange(float prevPitch, float prevYaw, float pitch, float yaw) {
        // Implementation credit goes to TheKodeToad
        // This project has the author's approval to use this
        // https://github.com/Sol-Client/Client/blob/main/game/src/main/java/io/github/solclient/client/mod/impl/hud/keystrokes/KeystrokesMod.java
        mouseX += (yaw - prevYaw) / 7F;
        mouseY += (pitch - prevPitch) / 7F;
        // 0, 0 will be the center of the HUD element
        float halfWidth = getWidth() / 2f;
        mouseX = MathHelper.clamp(mouseX, -halfWidth + 4, halfWidth - 4);
        mouseY = MathHelper.clamp(mouseY, -13, 13);
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
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        mouseX *= .75f;
        mouseY *= .75f;
    }

    @Override
    protected boolean getShadowDefault() {
        return false;
    }

    @Override
    public void renderPlaceholderComponent(float delta) {
        renderComponent(delta);
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
            float x = (strokeBounds.x() + stroke.offset.x() + ((float) strokeBounds.width() / 2)) -
                    ((float) client.textRenderer.getStringWidth(word) / 2);
            float y = strokeBounds.y() + stroke.offset.y() + ((float) strokeBounds.height() / 2) - 4;

            drawString(word, (int) x, (int) y, stroke.getFGColor().getAsInt(), shadow.get());
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
    public List<OptionBase<?>> getConfigurationOptions() {
        // We want a specific order since this is a more complicated entry
        List<OptionBase<?>> options = new ArrayList<>();
        options.add(enabled);
        options.add(scale);
        options.add(mouseMovement);
        options.add(textColor);
        options.add(pressedTextColor);
        options.add(shadow);
        options.add(background);
        options.add(backgroundColor);
        options.add(pressedBackgroundColor);
        options.add(outline);
        options.add(outlineColor);
        options.add(pressedOutlineColor);
        return options;
    }

    public void onMouseMovementOption(boolean value) {
        int baseHeight = 61;
        if (value) {
            baseHeight += 36;
        }
        height = baseHeight;
        onBoundsUpdate();
    }

    public class Keystroke {
        protected final KeyBinding key;
        protected final KeystrokeRenderer render;
        protected Rectangle bounds;
        protected DrawPosition offset;
        private float start = -1;
        private final int animTime = 100;
        private boolean wasPressed = false;

        public Keystroke(Rectangle bounds, DrawPosition offset, KeyBinding key, KeystrokeRenderer render) {
            this.bounds = bounds;
            this.offset = offset;
            this.key = key;
            this.render = render;
        }

        public void renderStroke() {
            if (key.isPressed() == wasPressed) {
                start = MinecraftClient.getTime();
            }
            Rectangle rect = bounds.offset(offset);
            if (background.get()) {
                fillRect(rect, getColor());
            }
            if (outline.get()) {
                outlineRect(rect, getOutlineColor());
            }
            if ((MinecraftClient.getTime() - start) / animTime >= 1) {
                start = -1;
            }
            wasPressed = key.isPressed();
        }

        private float getPercentPressed() {
            return start == -1 ? 1 : MathHelper.clamp((MinecraftClient.getTime() - start) / animTime, 0, 1);
        }

        public Color getColor() {

            return !key.isPressed() ? Color.blend(backgroundColor.get(), pressedBackgroundColor.get(),
                    getPercentPressed()) :
                   Color.blend(
                           pressedBackgroundColor.get(),
                           backgroundColor.get(),
                           getPercentPressed()
                   );
        }

        public Color getOutlineColor() {
            return !key.isPressed() ? Color.blend(outlineColor.get(), pressedOutlineColor.get(),
                    getPercentPressed()
            ) :
                   Color.blend(
                           pressedOutlineColor.get(),
                           outlineColor.get(),
                           getPercentPressed()
                   );
        }

        public Color getFGColor() {
            return !key.isPressed() ? Color.blend(textColor.get(), pressedTextColor.get(), getPercentPressed()) :
                   Color.blend(
                           pressedTextColor.get(),
                           textColor.get(),
                           getPercentPressed()
                   );
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
