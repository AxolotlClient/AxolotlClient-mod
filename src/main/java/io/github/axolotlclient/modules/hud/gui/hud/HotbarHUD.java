package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class HotbarHUD extends AbstractHudEntry {

    public static Identifier ID = new Identifier("axolotlclient", "hotbarhud");
    private static final Identifier WIDGETS = new Identifier("textures/gui/widgets.png");

    public HotbarHUD() {
        super(182, 22);
    }

    @Override
    protected double getDefaultX() {
        return 0.5;
    }

    @Override
    protected float getDefaultY() {
        return 0.9F;
    }

    @Override
    public void render() {
        if (this.client.getCameraEntity() instanceof PlayerEntity) {
            scale();
            DrawPosition pos = getPos();

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            this.client.getTextureManager().bindTexture(WIDGETS);
            PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
            float f = this.zOffset;
            this.zOffset = -90.0F;
            this.drawTexture(pos.x, pos.y, 0, 0, 182, 22);
            this.drawTexture(pos.x - 1 + playerEntity.inventory.selectedSlot * 20, pos.y - 1, 0, 22, 24, 22);
            this.zOffset = f;
            GlStateManager.enableRescaleNormal();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            DiffuseLighting.enable();

            for(int j = 0; j < 9; ++j) {
                int k = pos.x + j * 20 + 1;
                int l = pos.y+3;
                ItemUtil.renderGuiItem(playerEntity.inventory.main[j], k, l);
            }

            DiffuseLighting.disable();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();

            GlStateManager.popMatrix();
        }
    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        DrawPosition pos = getPos();

        drawCenteredString(MinecraftClient.getInstance().textRenderer, getName(), pos.x+width/2, pos.y+height/2-4, -1, true);

        GlStateManager.popMatrix();
        hovered=false;
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
    public void addConfigOptions(List<OptionBase<?>> options) {
        options.add(enabled);
    }
}
