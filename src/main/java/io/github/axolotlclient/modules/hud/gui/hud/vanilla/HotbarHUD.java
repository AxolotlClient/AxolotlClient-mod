package io.github.axolotlclient.modules.hud.gui.hud.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class HotbarHUD extends AbstractHudEntry {

    public static Identifier ID = new Identifier("axolotlclient", "hotbarhud");
    private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");

    public HotbarHUD() {
        super(182, 22);
    }

    @Override
    public void render(MatrixStack matrices, float delta) {
        PlayerEntity playerEntity = MinecraftClient.getInstance().cameraEntity instanceof PlayerEntity ? (PlayerEntity) MinecraftClient.getInstance().cameraEntity : null;
        if (playerEntity != null) {
            //scale(matrices);
            DrawPosition pos = getPos();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
            ItemStack itemStack = playerEntity.getOffHandStack();
            Arm arm = playerEntity.getMainArm().getOpposite();
            int j = this.getZOffset();
            this.setZOffset(-90);
            this.drawTexture(matrices, pos.x, pos.y, 0, 0, 182, 22);
            this.drawTexture(matrices, pos.x - 1 + playerEntity.getInventory().selectedSlot * 20, pos.y - 1, 0, 22, 24, 22);
            if (!itemStack.isEmpty()) {
                if (arm == Arm.LEFT) {
                    this.drawTexture(matrices, pos.x - 29, pos.y-1, 24, 22, 29, 24);
                } else {
                    this.drawTexture(matrices, pos.x + width, pos.y-1, 53, 22, 29, 24);
                }
            }

            this.setZOffset(j);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            for(int n = 0; n < 9; ++n) {
                int k = pos.x + n * 20 + 3;
                int l = pos.y+3;
                MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(playerEntity.getInventory().main.get(n), k, l);
                ItemUtil.renderGuiItemModel(getScale(), playerEntity.getInventory().main.get(n), k, l);
                ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, playerEntity.getInventory().main.get(n), k, l, null, -1,
                    true);
            }

            if (!itemStack.isEmpty()) {
                if (arm == Arm.LEFT) {
                    ItemUtil.renderGuiItemModel(getScale(), itemStack, pos.x - 26, pos.y + 3);
                    ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, itemStack, pos.x - 26, pos.y + 3, null, -1,
                        true);
                } else {
                    ItemUtil.renderGuiItemModel(getScale(), itemStack, pos.x + width + 10, pos.y + 3);
                    ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, itemStack, pos.x + width + 10, pos.y + 3, null, -1,
                        true);
                }
            }

            if (this.client.options.getAttackIndicator().get() == AttackIndicator.HOTBAR) {
                float f = this.client.player.getAttackCooldownProgress(0.0F);
                if (f < 1.0F) {
                    int o = pos.y + 2;
                    int p = pos.x + width + 6;
                    if (arm == Arm.RIGHT) {
                        p = pos.x - 22;
                    }

                    RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
                    int q = (int)(f * 19.0F);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    this.drawTexture(matrices, p, o, 0, 94, 18, 18);
                    this.drawTexture(matrices, p, o + 18 - q, 18, 112 - q, 18, q);
                }
            }

            RenderSystem.disableBlend();
            //matrices.pop();
        }
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices, float delta) {
        renderPlaceholderBackground(matrices);
        scale(matrices);
        DrawPosition pos = getPos();

        drawCenteredString(matrices, MinecraftClient.getInstance().textRenderer, getName(), pos.x+width/2, pos.y+height/2-4, -1, true);

        matrices.pop();
        hovered = false;
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
    public boolean overridesF3() {
        return true;
    }

    @Override
    public List<OptionBase<?>> getConfigurationOptions() {
        List<OptionBase<?>> list = new ArrayList<>();
        list.add(enabled);
        return list;
    }
}
