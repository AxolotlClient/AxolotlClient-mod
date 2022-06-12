package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import io.github.moehreag.axolotlclient.modules.hypixel.levelhead.LevelHead;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    @Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I", ordinal = 0))
    public void addBadges(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
        if(entity instanceof AbstractClientPlayerEntity && text.getString().contains(entity.getName().getString()))
            AxolotlClient.addBadge(entity, matrices);
    }

    @Redirect(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
    public int forceShadows(TextRenderer instance, Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light){
        return instance.draw(text, x, y, color, AxolotlClient.CONFIG.useShadows.get(), matrix, vertexConsumers, seeThrough, AxolotlClient.CONFIG.nametagBackground.get()? backgroundColor : 0, light);
    }

    @Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I", ordinal = 1))
    public void addLevel(T entity, Text string, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
        if(entity instanceof AbstractClientPlayerEntity){
            if(MinecraftClient.getInstance().getCurrentServerEntry() != null &&
                    MinecraftClient.getInstance().getCurrentServerEntry().address.contains("hypixel.net")){
                if(HypixelAbstractionLayer.hasValidAPIKey() && LevelHead.getInstance().enabled.get() && string.getString().contains(entity.getName().getString())){
                    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                    String text = "Level: "+ HypixelAbstractionLayer.getPlayerLevel(String.valueOf(entity.getUuid()));

                    float x = textRenderer.getWidth(text)/2F;
                    float y = string.getString().contains("deadmau5")?-20:-10;

					Matrix4f matrix4f = matrices.peek().getPosition();
	                MinecraftClient.getInstance().textRenderer.draw(text, x, y, LevelHead.getInstance().textColor.get().getAsInt(), AxolotlClient.CONFIG.useShadows.get(), matrix4f, vertexConsumers, false, LevelHead.getInstance().background.get()? 127 : 0, light);

                } else if(!HypixelAbstractionLayer.hasValidAPIKey()){
                    HypixelAbstractionLayer.loadApiKey();
                }
            }
        }
    }

}
