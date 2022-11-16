package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.gui.layout.CardinalOrder;
import io.github.axolotlclient.modules.hud.util.DefaultOptions;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class PotionsHud extends TextHudEntry implements DynamicallyPositionable {

    public static final Identifier ID = new Identifier("kronhud", "potionshud");

    private final EnumOption anchor = DefaultOptions.getAnchorPoint();

    private final EnumOption order = DefaultOptions.getCardinalOrder(CardinalOrder.TOP_DOWN);

    private final BooleanOption iconsOnly = new BooleanOption("iconsonly", ID.getPath(), false);

    public PotionsHud() {
        super(50, 200, false);
    }

    private int calculateWidth(List<StatusEffectInstance> effects) {
        if (CardinalOrder.valueOf(order.get()).isXAxis()) {
            if (iconsOnly.get()) {
                return 20 * effects.size() + 2;
            }
            return 50 * effects.size() + 2;
        } else {
            if (iconsOnly.get()) {
                return 20;
            }
            return 50;
        }
    }

    private int calculateHeight(List<StatusEffectInstance> effects) {
        if (CardinalOrder.valueOf(order.get()).isXAxis()) {
            return 22;
        } else {
            return 20 * effects.size() + 2;
        }
    }

    @Override
    public void renderComponent(MatrixStack matrices, float delta) {
        List<StatusEffectInstance> effects = new ArrayList<>(client.player.getStatusEffects());
        if (effects.isEmpty()) {
            return;
        }
        renderEffects(matrices, effects);
    }

    private void renderEffects(MatrixStack matrices, List<StatusEffectInstance> effects) {
        int calcWidth = calculateWidth(effects);
        int calcHeight = calculateHeight(effects);
        boolean changed = false;
        if (calcWidth != width) {
            setWidth(calcWidth);
            changed = true;
        }
        if (calcHeight != height) {
            setHeight(calcHeight);
            changed = true;
        }
        if (changed) {
            onBoundsUpdate();
        }
        int lastPos = 0;
        CardinalOrder direction = CardinalOrder.valueOf(order.get());

        Rectangle bounds = getBounds();
        int x = bounds.x();
        int y = bounds.y();
        for (int i = 0; i < effects.size(); i++) {
            StatusEffectInstance effect = effects.get(direction.getDirection() == -1 ? i : effects.size() - i - 1);
            if (direction.isXAxis()) {
                renderPotion(matrices, effect, x + lastPos + 1, y + 1);
                lastPos += (iconsOnly.get() ? 20 : 50);
            } else {
                renderPotion(matrices, effect, x + 1, y + 1 + lastPos);
                lastPos += 20;
            }
        }
    }

    private void renderPotion(MatrixStack matrices, StatusEffectInstance effect, int x, int y) {
        StatusEffect type = effect.getEffectType();
        Sprite sprite = client.getStatusEffectSpriteManager().getSprite(type);

        RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        DrawableHelper.drawSprite(matrices, x, y, 0, 18, 18, sprite);
        if (!iconsOnly.get()) {
            drawString(matrices, StatusEffectUtil.durationToString(effect, 1), x + 19, y + 5,
                    textColor.get().getAsInt(), shadow.get()
            );
        }
    }

    @Override
    public void renderPlaceholderComponent(MatrixStack matrices, float delta) {
        StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.SPEED);
        StatusEffectInstance jump = new StatusEffectInstance(StatusEffects.JUMP_BOOST);
        StatusEffectInstance haste = new StatusEffectInstance(StatusEffects.HASTE);
        renderEffects(matrices, List.of(effect, jump, haste));
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(anchor);
        options.add(order);
        options.add(iconsOnly);
        return options;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public AnchorPoint getAnchor() {
        return AnchorPoint.valueOf(anchor.get());
    }
}
