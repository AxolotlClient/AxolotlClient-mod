package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.config.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Objects;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class ArmorHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "armorhud");

	protected BooleanOption showProtLvl = new BooleanOption("showProtectionLevel", false);

    public ArmorHud() {
        super(20, 100);
    }

	@Override
	public void render(MatrixStack matrices) {
		matrices.push();
		scale(matrices);
		DrawPosition pos = getPos();

		if (background.get()) {
			fillRect(matrices, getBounds(),
				backgroundColor.get());
		}
		if(outline.get()) outlineRect(matrices, getBounds(), outlineColor.get());
		int lastY = 2 + (4 * 20);
		assert client.player != null;
		renderMainItem(matrices, client.player.getInventory().getMainHandStack().copy(), pos.x + 2, pos.y + lastY);
		lastY = lastY - 20;
		for (int i = 0; i <= 3; i++) {
			ItemStack item = client.player.getInventory().armor.get(i);
			renderItem(matrices, item.copy(), pos.x + 2, lastY + pos.y);
			lastY = lastY - 20;
		}
		matrices.pop();
	}

	public void renderItem(MatrixStack matrices, ItemStack stack, int x, int y) {

		if(showProtLvl.get() && stack.hasEnchantments()) {
			for (int i = 0; i < stack.getEnchantments().size(); ++i) {
				NbtCompound nbtCompound = stack.getEnchantments().getCompound(i);
				Registry.ENCHANTMENT
					.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound))
					.ifPresent(e -> {
						if (Objects.equals(e.getTranslationKey(), Enchantments.PROTECTION.getTranslationKey())) {
							stack.setCount(EnchantmentHelper.getLevelFromNbt(nbtCompound));
						}
					});
			}
		}
		ItemUtil.renderGuiItemModel(matrices, stack, x, y);
		ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, stack, x, y, null, textColor.get().getAsInt(),
			shadow.get());
	}

	public void renderMainItem(MatrixStack matrices, ItemStack stack, int x, int y) {
		ItemUtil.renderGuiItemModel(matrices, stack, x, y);
		String total = String.valueOf(ItemUtil.getTotal(client, stack));
		if (total.equals("1")) {
			total = null;
		}
		ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, stack, x, y,
			total, textColor.get().getAsInt(),
			shadow.get());
	}

	@Override
	public void renderPlaceholder(MatrixStack matrices) {
		matrices.push();
		renderPlaceholderBackground(matrices);
		scale(matrices);
		DrawPosition pos = getPos();
		int lastY = 2 + (4 * 20);
		ItemUtil.renderGuiItemModel(matrices, new ItemStack(Items.GRASS_BLOCK), pos.x + 2, pos.y + lastY);
		ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, new ItemStack(Items.GRASS_BLOCK), pos.x + 2,
			pos.y + lastY, "90", textColor.get().getAsInt(), shadow.get());
		hovered = false;
		matrices.pop();
	}

    @Override
    public boolean movable() {
        return true;
    }

	@Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void addConfigOptions(List<OptionBase<?>> options) {
        super.addConfigOptions(options);
        options.add(textColor);
        options.add(shadow);
        options.add(background);
        options.add(backgroundColor);
        options.add(outline);
        options.add(outlineColor);
		options.add(showProtLvl);
    }

}
