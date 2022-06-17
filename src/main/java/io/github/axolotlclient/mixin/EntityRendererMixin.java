package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import io.github.axolotlclient.modules.hypixel.levelhead.LevelHead;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    @Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;III)I"))
    public void addBadges(T entity, String string, double d, double e, double f, int i, CallbackInfo ci){
        if(entity instanceof AbstractClientPlayerEntity && string.contains(entity.method_6344().asFormattedString()))
            AxolotlClient.addBadge(entity);
    }

    @Redirect(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;III)I"))
    public int forceShadows(TextRenderer instance, String text, int x, int y, int color){
        instance.draw(text, x, y, color, AxolotlClient.CONFIG.useShadows.get());
        return 0;
    }

    @Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;III)I", ordinal = 1))
    public void addLevel(T entity, String string, double d, double e, double f, int i, CallbackInfo ci){
        if(entity instanceof AbstractClientPlayerEntity){
            if(MinecraftClient.getInstance().getCurrentServerEntry() != null &&
                    MinecraftClient.getInstance().getCurrentServerEntry().address.contains("hypixel.net")){
                if(HypixelAbstractionLayer.hasValidAPIKey() && LevelHead.getInstance().enabled.get() && string.contains(entity.method_6344().asFormattedString())){
                    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                    String text = "Level: "+ HypixelAbstractionLayer.getPlayerLevel(String.valueOf(entity.getUuid()));

                    float x = textRenderer.getStringWidth(text)/2F;
                    int y = string.contains("deadmau5")?-20:-10;

                    if(LevelHead.getInstance().background.get()){
                        Tessellator tessellator = Tessellator.getInstance();
                        BufferBuilder bufferBuilder = tessellator.getBuffer();
                        GlStateManager.disableTexture();
                        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
                        bufferBuilder.vertex(-x - 1, -1 + y, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
                        bufferBuilder.vertex(-x - 1, 8 + y, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
                        bufferBuilder.vertex(x + 1, 8 + y, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
                        bufferBuilder.vertex(x + 1, -1 + y, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
                        tessellator.draw();
                        GlStateManager.enableTexture();
                    }

                    textRenderer.draw(text, -x, y,  LevelHead.getInstance().textColor.get().getAsInt(), AxolotlClient.CONFIG.useShadows.get());
                } else if(!HypixelAbstractionLayer.hasValidAPIKey()){
                    HypixelAbstractionLayer.loadApiKey();
                }
            }
        }
    }

    @Redirect(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;vertex(DDD)Lnet/minecraft/client/render/BufferBuilder;"))
    public BufferBuilder noBg(BufferBuilder instance, double d, double e, double f){
        if(AxolotlClient.CONFIG.nametagBackground.get()){
            instance.vertex(d, e, f);
        }
        return instance;
    }

}
