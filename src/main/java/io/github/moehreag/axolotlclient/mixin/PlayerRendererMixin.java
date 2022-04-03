package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntityRenderer.class)
public class PlayerRendererMixin {

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
