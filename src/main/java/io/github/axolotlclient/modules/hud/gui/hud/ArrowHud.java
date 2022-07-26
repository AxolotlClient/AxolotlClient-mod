package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class ArrowHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "arrowhud");
    private int arrows = 0;
    private final BooleanOption dynamic = new BooleanOption("dynamic", false);
	private final BooleanOption allArrowTypes = new BooleanOption("allArrowTypes", false);
	private ItemStack currentArrow = new ItemStack(Items.ARROW);

    public ArrowHud() {
        super(20, 30);
    }

	@Override
	public void render(MatrixStack matrices) {
		if (dynamic.get()) {
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (!(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof RangedWeaponItem
				|| player.getStackInHand(Hand.OFF_HAND).getItem() instanceof RangedWeaponItem)) {
				return;
			}
		}
		matrices.push();
		scale(matrices);

		DrawPosition pos = getPos();
		if (background.get()) {
			fillRect(matrices, getBounds(), backgroundColor.get());
		}
		if(outline.get()) outlineRect(matrices, getBounds(), outlineColor.get());
		drawCenteredString(matrices, client.textRenderer, String.valueOf(arrows), new DrawPosition(pos.x + width / 2,
			pos.y + height - 10), textColor.get(), shadow.get());
		ItemUtil.renderGuiItemModel(matrices, currentArrow, pos.x + 2, pos.y + 2);
		matrices.pop();
	}

	@Override
	public boolean tickable() {
		return true;
	}

	@Override
	public void tick() {
		if (allArrowTypes.get()) {
			arrows = ItemUtil.getTotal(client, new ItemStack(Items.ARROW)) + ItemUtil.getTotal(client, new ItemStack(Items.TIPPED_ARROW)) + ItemUtil.getTotal(client, new ItemStack(Items.SPECTRAL_ARROW));
		} else {
			arrows = ItemUtil.getTotal(client, currentArrow);
		}
		if (client.player == null) {
			return;
		}
		if (!allArrowTypes.get()) {
			currentArrow = client.player.getArrowType(Items.BOW.getDefaultStack());
		} else {
			currentArrow = new ItemStack(Items.ARROW);
		}
	}

	@Override
	public void renderPlaceholder(MatrixStack matrices) {
		matrices.push();
		renderPlaceholderBackground(matrices);
		scale(matrices);
		DrawPosition pos = getPos();
		drawCenteredString(matrices, client.textRenderer, "64", new DrawPosition(pos.x + width / 2,
			pos.y + height - 10), textColor.get(), shadow.get());
		ItemUtil.renderGuiItemModel(matrices, new ItemStack(Items.ARROW), pos.x + 2, pos.y + 2);
		hovered = false;
		matrices.pop();
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
        options.add(dynamic);
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
