package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "hasLabel*", at = @At("HEAD"), cancellable = true)
    private void showOwnNametag(T livingEntity, CallbackInfoReturnable<Boolean> cir){
        if ( Axolotlclient.CONFIG.NametagConf.showOwnNametag && livingEntity == MinecraftClient.getInstance().player) {
            cir.setReturnValue(true);
        }
    }


    @Inject(method = "method_10208(Lnet/minecraft/entity/LivingEntity;DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;III)I"))
    public void addBadge(T livingEntity, double d, double e, double f, CallbackInfo ci){
        Axolotlclient.addBadge(livingEntity, d, e, f);
    }

}
