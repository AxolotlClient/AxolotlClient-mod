package io.github.axolotlclient.modules.hud.gui.hud.vanilla;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlclientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.util.Util;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class CrosshairHud extends AbstractHudEntry implements DynamicallyPositionable {
    public static final Identifier ID = new Identifier("kronhud", "crosshairhud");

    private final EnumOption type = new EnumOption("axolotlclient.crosshair_type", Crosshair.values(), Crosshair.CROSS.toString());
    private final BooleanOption showInF5 = new BooleanOption("axolotlclient.showInF5", false);
    private final ColorOption defaultColor = new ColorOption("axolotlclient.defaultcolor", Color.WHITE);
    private final ColorOption entityColor = new ColorOption("axolotlclient.entitycolor", Color.SELECTOR_RED);
    private final ColorOption containerColor = new ColorOption("axolotlclient.blockcolor",Color.SELECTOR_BLUE);

    public CrosshairHud() {
        super(15, 15);
    }

    @Override
    public double getDefaultX() {
        return 0.5;
    }

    @Override
    public double getDefaultY() {
        return 0.5F;
    }

    @Override
    public void render(float delta) {
        if (!(client.options.perspective == 0) && !showInF5.get()) return;

        GlStateManager.enableAlphaTest();

        GlStateManager.pushMatrix();
        scale();
        Color color = getColor();
        GlStateManager.color4f((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, 1F);
        if(color==defaultColor.get()) {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(775, 769, 1, 0);
        }

        DrawPosition pos = getPos().subtract(0, -1);
        if (type.get().equals(Crosshair.DOT.toString())) {

            fillRect(pos.x + (width / 2) - 1, pos.y + (height / 2) - 2, 3, 3, color.getAsInt());
        } else if (type.get().equals(Crosshair.CROSS.toString())) {

            fillRect(pos.x + (width / 2) - 5, pos.y + (height / 2) - 1, 6, 1, color.getAsInt());
            fillRect(pos.x + (width / 2) + 1, pos.y + (height / 2) - 1, 5, 1, color.getAsInt());
            fillRect(pos.x + (width / 2), pos.y + (height / 2) - 6, 1, 6, color.getAsInt());
            fillRect(pos.x + (width / 2), pos.y + (height / 2), 1, 5, color.getAsInt());
        } else if (type.get().equals(Crosshair.TEXTURE.toString())) {

            MinecraftClient.getInstance().getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);

            // Draw crosshair
            //GlStateManager.color4f((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, 1F);
            client.inGameHud.drawTexture((int) (((Util.getWindow().getScaledWidth() / getScale()) - 14) / 2),
                    (int) (((Util.getWindow().getScaledHeight() / getScale()) - 14) / 2), 0, 0, 16, 16);


        }
        GlStateManager.color4f(1, 1, 1, 1);
        GlStateManager.blendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableBlend();
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
    public void renderPlaceholder(float delta) {
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
    public List<OptionBase<?>> getConfigurationOptions() {
        List<OptionBase<?>> options = super.getConfigurationOptions();
        options.add(type);
        options.add(showInF5);
        options.add(defaultColor);
        options.add(entityColor);
        options.add(containerColor);
        return options;
    }

    @Override
    public AnchorPoint getAnchor() {
        return AnchorPoint.MIDDLE_MIDDLE;
    }

    public enum Crosshair {
        CROSS,
        DOT,
        TEXTURE
    }
}
