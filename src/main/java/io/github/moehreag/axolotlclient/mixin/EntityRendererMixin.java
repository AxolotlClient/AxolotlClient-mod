package io.github.moehreag.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.Axolotlclient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.resource.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    @Inject(method = "method_6917", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;III)I"))
    public void addBadges(T entity, String string, double d, double e, double f, int i, CallbackInfo ci){
        if(entity instanceof AbstractClientPlayerEntity){
            if(Axolotlclient.CONFIG.badgeOptions.showBadge && Axolotlclient.isUsingClient(entity.getUuid())) {
                try {
                    Resource badge = MinecraftClient.getInstance().getResourceManager().getResource(Axolotlclient.FONT);
                    MinecraftClient.getInstance().getTextureManager().bindTexture(badge.getId());

                    int x = -(MinecraftClient.getInstance().textRenderer.getStringWidth(string)/2 + (Axolotlclient.CONFIG.badgeOptions.CustomBadge ? MinecraftClient.getInstance().textRenderer.getStringWidth(Axolotlclient.CONFIG.badgeOptions.badgeText): 10));

                    GlStateManager.color4f(1, 1, 1, 1);

                    if(Axolotlclient.CONFIG.badgeOptions.CustomBadge) MinecraftClient.getInstance().textRenderer.draw(Axolotlclient.CONFIG.badgeOptions.badgeText, x, 0 ,0, Axolotlclient.CONFIG.NametagConf.useShadows);
                    else DrawableHelper.drawTexture(x, 0, 0, 0, 8, 8, 8, 8);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Redirect(method = "method_6917", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;III)I"))
    public int forceShadows(TextRenderer instance, String text, int x, int y, int color){
        instance.draw(text, x, y, color, Axolotlclient.CONFIG.NametagConf.useShadows);
        return 0;
    }

}
