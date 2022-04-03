package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
    private BooleanOption dynamic = new BooleanOption("dynamic", false);
    private BooleanOption allArrowTypes = new BooleanOption("allArrowTypes", false);
    private ItemStack currentArrow = new ItemStack(Items.ARROW);

    public ArrowHud() {
        super(20, 30);
    }

    @Override
    public void render() {
        /*if (dynamic.getBooleanValue()) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (!(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof RangedWeaponItem
                    || player.getStackInHand(Hand.OFF_HAND).getItem() instanceof RangedWeaponItem)) {
                return;
            }
        }
        matrices.push();
        scale(matrices);

        DrawPosition pos = getPos();
        if (background.getBooleanValue()) {
            fillRect(matrices, getBounds(), backgroundColor.getColor());
        }
        drawCenteredString(matrices, client.textRenderer, String.valueOf(arrows), new DrawPosition(pos.x() + width / 2,
                pos.y() + height - 10), textColor.getColor(), shadow.getBooleanValue());
        ItemUtil.renderGuiItemModel(matrices, currentArrow, pos.x() + 2, pos.y() + 2);
        matrices.pop();*/
    }

    /*@Override
    public boolean tickable() {
        return true;
    }*/

    /*@Override
    public void tick() {
        if (allArrowTypes.getBooleanValue()) {
            arrows = ItemUtil.getTotal(client, new ItemStack(Items.ARROW)) + ItemUtil.getTotal(client, new ItemStack(Items.TIPPED_ARROW)) + ItemUtil.getTotal(client, new ItemStack(Items.SPECTRAL_ARROW));
        } else {
            arrows = ItemUtil.getTotal(client, currentArrow);
        }
        if (client.player == null) {
            return;
        }
        if (!allArrowTypes.getBooleanValue()) {
            currentArrow = client.player.getArrowType(Items.BOW.getDefaultStack());
        } else {
            currentArrow = new ItemStack(Items.ARROW);
        }
    }*/

    @Override
    public void renderPlaceholder() {
        /*matrices.push();
        renderPlaceholderBackground(matrices);
        scale(matrices);
        DrawPosition pos = getPos();
        drawCenteredString(matrices, client.textRenderer, "64", new DrawPosition(pos.x() + width / 2,
                pos.y() + height - 10), textColor.getColor(), shadow.getBooleanValue());
        ItemUtil.renderGuiItemModel(matrices, new ItemStack(Items.ARROW), pos.x() + 2, pos.y() + 2);
        hovered = false;
        matrices.pop();*/
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        //options.add(textColor);
        options.add(shadow);
        options.add(background);
        //options.add(backgroundColor);
        options.add(dynamic);
        options.add(allArrowTypes);
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
