package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.enumOptions.CrosshairHudOption;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class CrosshairHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "crosshairhud");

    private final CrosshairHudOption type = new CrosshairHudOption("type");
    private final BooleanOption showInF5 = new BooleanOption("showInF5", false);
    public final BooleanOption showInF3 = new BooleanOption("showInF3", false);
    private final ColorOption defaultColor = new ColorOption("defaultcolor",  "#FFFFFFFF");
    private final ColorOption entityColor = new ColorOption("entitycolor", Color.SELECTOR_RED);
    private final ColorOption containerColor = new ColorOption("blockcolor", Color.SELECTOR_BLUE);

    public CrosshairHud() {
        super(17, 17);
    }

    @Override
    protected double getDefaultX() {
        return 0.5;
    }

    //Direction is not available since it's implemented completely different and I couldn't get it to work...
    @Override
    protected float getDefaultY() {
        return 0.5F;
    }

    @Override
    public void render() {
        if (!(client.options.perspective == 0) && !showInF5.get()) return;

        GlStateManager.enableAlphaTest();
        scale();

        Color color = getColor();
        GlStateManager.color4f((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, 1F);
        GlStateManager.blendFuncSeparate(775, 769, 1, 0);
        DrawPosition pos = getPos().subtract(0, -1);
        if (type.get() == CrosshairHudOption.CrosshairOption.DOT) {

            fillRect(new Rectangle(pos.x + (width / 2) - 1, pos.y + (height / 2) - 2, 3, 3), color);
        } else if (type.get() == CrosshairHudOption.CrosshairOption.CROSS) {

            fillRect(new Rectangle(pos.x + (width / 2) - 5, pos.y + (height / 2) - 1, 6, 1), color);
            fillRect(new Rectangle(pos.x + (width / 2) + 1, pos.y + (height / 2) - 1, 5, 1), color);
            fillRect(new Rectangle(pos.x + (width / 2), pos.y + (height / 2) - 6, 1, 6), color);
            fillRect(new Rectangle(pos.x + (width / 2), pos.y + (height / 2), 1, 5), color);
        } else if (type.get() == CrosshairHudOption.CrosshairOption.TEXTURE) {

            MinecraftClient.getInstance().getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);

            // Draw crosshair
            //GlStateManager.color4f((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, 1F);
            client.inGameHud.drawTexture((int) (((new Window(client).getScaledWidth() / getScale()) - 14) / 2),
                    (int) (((new Window(client).getScaledHeight() / getScale()) - 14) / 2), 0, 0, 16, 16);


        }
        GlStateManager.popMatrix();
        GlStateManager.disableAlphaTest();

    }

    public Color getColor() {
        BlockHitResult hit = client.result;
        if (hit == null || hit.type == null) {
            return defaultColor.get();
        } else if (hit.type == BlockHitResult.Type.ENTITY) {
            return entityColor.get();
        } else if (hit.type == BlockHitResult.Type.BLOCK) {
            BlockPos blockPos = hit.getBlockPos();
            World world = this.client.world;
            if (world.getBlockState(blockPos).getBlock() != null &&
                    (world.getBlockState(blockPos).getBlock() instanceof ChestBlock ||
                            world.getBlockState(blockPos).getBlock() instanceof EnderChestBlock ||
                            world.getBlockState(blockPos).getBlock() instanceof HopperBlock)) {
                return containerColor.get();
            }
        }
        return defaultColor.get();
    }
    @Override
    public void renderPlaceholder() {
        // Shouldn't need this...
    }

    @Override
    public boolean movable() {
        return false;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        options.add(type);
        options.add(showInF5);
        options.add(showInF3);
        options.add(defaultColor);
        options.add(entityColor);
        options.add(containerColor);
    }

}
