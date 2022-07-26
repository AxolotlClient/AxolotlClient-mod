package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class PotionsHud extends AbstractHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "potionshud");

	public PotionsHud() {
        super(60, 200);
    }

	@Override
	public void render(MatrixStack matrices) {
		matrices.push();
		scale(matrices);
		ArrayList<StatusEffectInstance> effects = new ArrayList<>(client.player.getStatusEffects());
		if (!effects.isEmpty()) {
			StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
			int lastY = 1;
			DrawPosition pos = getPos();
			for (int i = 0; i < effects.size(); i++) {
				StatusEffectInstance effect = effects.get(i);
				StatusEffect type = effect.getEffectType();
				if (i > 8) {
					break;
				}

				Sprite sprite = statusEffectSpriteManager.getSprite(type);
				MinecraftClient.getInstance().getTextureManager().bindTexture(sprite.getAtlas().getId());
				RenderSystem.color4f(1, 1, 1, 1);
				DrawableHelper.drawSprite(matrices, pos.x, pos.y + 1 + lastY, 0, 18, 18, sprite);

                MutableText text = type.getName().copy();
                if (effect.getAmplifier() == 2) {
                    text.append(" ").append(new TranslatableText("enchantment.level.2"));
                } else if (effect.getAmplifier() == 3) {
                    text.append(" ").append(new TranslatableText("enchantment.level.3"));
                } else if (effect.getAmplifier() == 4) {
                    text.append(" ").append(new TranslatableText("enchantment.level.4"));
                }

                drawTextWithShadow(matrices, client.textRenderer, text, pos.x + 20, pos.y + 4 + lastY, textColor.get().getAsInt());
				drawString(matrices, client.textRenderer, StatusEffectUtil.durationToString(effect, 1),
					pos.x + 20+4, pos.y + 6 + 10 + lastY, 8355711, shadow.get());

				lastY += 33;
			}
		}
		matrices.pop();

	}

	@Override
	public void renderPlaceholder(MatrixStack matrices) {
		matrices.push();
		renderPlaceholderBackground(matrices);
		scale(matrices);
		DrawPosition pos = getPos();
		StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
		StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.SPEED);
		StatusEffect type = effect.getEffectType();
		Sprite sprite = statusEffectSpriteManager.getSprite(type);
		MinecraftClient.getInstance().getTextureManager().bindTexture(sprite.getAtlas().getId());
		RenderSystem.color4f(1, 1, 1, 1);
		DrawableHelper.drawSprite(matrices, pos.x + 1, pos.y + 1, 0, 18, 18, sprite);
        MutableText text = type.getName().copy();
        if (effect.getAmplifier() == 2) {
            text.append(" ").append(new TranslatableText("enchantment.level.2"));
        } else if (effect.getAmplifier() == 3) {
            text.append(" ").append(new TranslatableText("enchantment.level.3"));
        } else if (effect.getAmplifier() == 4) {
            text.append(" ").append(new TranslatableText("enchantment.level.4"));
        }

        drawTextWithShadow(matrices, client.textRenderer, text, pos.x + 20, pos.y + 7, Color.WHITE.getAsInt());
        drawString(matrices, client.textRenderer, StatusEffectUtil.durationToString(effect, 1),
            pos.x + 20+4, pos.y + 7 + 10, Color.WHITE.getAsInt(), shadow.get());
		hovered = false;
		matrices.pop();
	}

    @Override
    public void addConfigOptions(List<OptionBase<?>> options) {
        super.addConfigOptions(options);
        options.add(textColor);
        options.add(shadow);
    }

    @Override
    public boolean movable() {
        return true;
    }

	@Override
    public Identifier getId() {
        return ID;
    }

}
