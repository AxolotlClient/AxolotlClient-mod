package io.github.moehreag.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.modules.levelhead.HypixelAbstractionLayer;
import io.github.moehreag.axolotlclient.modules.levelhead.LevelHead;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity> {
    public PlayerRendererMixin(EntityRenderDispatcher dispatcher, EntityModel model, float shadowSize) {
        super(dispatcher, model, shadowSize);
    }


    @ModifyArgs(method = "method_10209(Lnet/minecraft/client/network/AbstractClientPlayerEntity;DDDLjava/lang/String;FD)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;method_10209(Lnet/minecraft/entity/Entity;DDDLjava/lang/String;FD)V"))
    public void modifiyName(Args args){
        if(Axolotlclient.CONFIG != null) {
            AbstractClientPlayerEntity player = args.get(0);
            if (Axolotlclient.CONFIG.hideNames.get()) {
                assert MinecraftClient.getInstance().player != null;
                if (player.getName() != MinecraftClient.getInstance().player.getName()) {
                    args.set(4, Axolotlclient.CONFIG.name);
                }
            }
        }
    }


    @Inject(method = "method_10209(Lnet/minecraft/client/network/AbstractClientPlayerEntity;DDDLjava/lang/String;FD)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;method_10209(Lnet/minecraft/entity/Entity;DDDLjava/lang/String;FD)V"))
    public void renderLevelHead(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, String string, float g, double h, CallbackInfo ci){
        if(MinecraftClient.getInstance().getCurrentServerEntry() != null &&
            MinecraftClient.getInstance().getCurrentServerEntry().address.contains("hypixel.net")){
            if(HypixelAbstractionLayer.hasValidAPIKey() && LevelHead.getInstance().enabled.get()){
                GlStateManager.pushMatrix();
                GlStateManager.translated(0, 0.25, 0);

                renderCustomNametag(abstractClientPlayerEntity, "Level: "+ HypixelAbstractionLayer.getPlayerLevel(abstractClientPlayerEntity.getUuid().toString()), 64, LevelHead.getInstance().textColor.get().getAsInt());

                GlStateManager.popMatrix();
            }
        }
    }

    private void renderCustomNametag(PlayerEntity entity, String string, int light, int color){
        double g = entity.squaredDistanceTo(this.dispatcher.field_11098);
        if (!(g > (double)(light * light))) {
            TextRenderer textRenderer = this.getFontRenderer();
            float j = 0.016666668F * 1.6F;
            GlStateManager.pushMatrix();
            //GlStateManager.translatef((float)d + 0.0F, (float)e + entity.height + 0.5F, (float)f);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(-this.dispatcher.field_2102, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(this.dispatcher.field_2103, 1.0F, 0.0F, 0.0F);
            GlStateManager.scalef(-j, -j, j);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepthTest();
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            int y = 0;
            if (string.equals("deadmau5")) {
                y = -10;
            }

            if(LevelHead.getInstance().background.get()) {
            int centerX = textRenderer.getStringWidth(string) / 2;
                GlStateManager.disableTexture();
                bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(-centerX - 1, -1 + y, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
                bufferBuilder.vertex(-centerX - 1, 8 + y, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
                bufferBuilder.vertex(centerX + 1, 8 + y, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
                bufferBuilder.vertex(centerX + 1, -1 + y, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
                tessellator.draw();
                GlStateManager.enableTexture();
            }

          // textRenderer.draw(string, -textRenderer.getStringWidth(string) / 2, y, 553648127);
            GlStateManager.enableDepthTest();
            GlStateManager.depthMask(true);
            if(entity.isSneaking())textRenderer.draw(string, -textRenderer.getStringWidth(string) / 2, 0, 553648127);
            else  textRenderer.drawWithShadow(string, -textRenderer.getStringWidth(string) / (float)2, y, color);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }
}
