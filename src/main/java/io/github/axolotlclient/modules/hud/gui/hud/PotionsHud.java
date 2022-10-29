package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.gui.layout.CardinalOrder;
import io.github.axolotlclient.modules.hud.util.DefaultOptions;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PotionsHud extends TextHudEntry implements DynamicallyPositionable {

    public static final Identifier ID = new Identifier("kronhud", "potionshud");

    private final EnumOption anchor = DefaultOptions.getAnchorPoint();

    private final EnumOption order = DefaultOptions.getCardinalOrder(CardinalOrder.TOP_DOWN);

    private final BooleanOption iconsOnly = new BooleanOption("axolotlclient.iconsonly", false);
    protected static final Identifier INVENTORY_TEXTURE = new Identifier("textures/gui/container/inventory.png");

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
    public void renderComponent(float delta) {
        List<StatusEffectInstance> effects = new ArrayList<>(client.player.getStatusEffectInstances());
        if (effects.isEmpty()) {
            return;
        }
        renderEffects(effects);
    }

    private void renderEffects(List<StatusEffectInstance> effects) {
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
                renderPotion(effect, x + lastPos + 1, y + 1);
                lastPos += (iconsOnly.get() ? 20 : 50);
            } else {
                renderPotion(effect, x + 1, y + 1 + lastPos);
                lastPos += 20;
            }
        }
    }

    private void renderPotion(StatusEffectInstance effect, int x, int y) {
        StatusEffect type = StatusEffect.STATUS_EFFECTS[effect.getEffectId()];
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(INVENTORY_TEXTURE);
        int m = type.method_2444();
        this.drawTexture(x, y, m % 8 * 18, 198 + m / 8 * 18, 18, 18);
        if (!iconsOnly.get()) {
            drawString(StatusEffect.method_2436(effect), x + 19, y + 5,
                    textColor.get().getAsInt(), shadow.get()
            );
        }
    }

    @Override
    public void renderPlaceholderComponent(float delta) {
        StatusEffectInstance effect = new StatusEffectInstance(StatusEffect.SPEED.id, 9999);
        StatusEffectInstance jump = new StatusEffectInstance(StatusEffect.JUMP_BOOST.id, 99999);
        StatusEffectInstance haste = new StatusEffectInstance(StatusEffect.HASTE.id, 9999999);
        List<StatusEffectInstance> list = new ArrayList<>();
        list.add(effect);
        list.add(jump);
        list.add(haste);
        renderEffects(list);
    }

    @Override
    public List<OptionBase<?>> getConfigurationOptions() {
        List<OptionBase<?>> options = super.getConfigurationOptions();
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
