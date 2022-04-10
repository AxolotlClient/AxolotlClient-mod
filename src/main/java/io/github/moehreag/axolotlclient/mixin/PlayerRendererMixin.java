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
import net.minecraft.entity.player.PlayerEntity;
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
}
