package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "hasLabel*", at = @At("HEAD"), cancellable = true)
    private void showOwnNametag(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir){
        if (AxolotlClient.CONFIG.showOwnNametag.get() && livingEntity == MinecraftClient.getInstance().player) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "method_10208(Lnet/minecraft/entity/LivingEntity;DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;method_6344()Lnet/minecraft/text/Text;"))
    public Text hideNameWhenSneaking(LivingEntity instance){
        if(instance instanceof AbstractClientPlayerEntity) {

            if (NickHider.Instance.hideOwnName.get() && instance.equals(MinecraftClient.getInstance().player)){
                return new LiteralText(NickHider.Instance.hiddenNameSelf.get());
            } else if (NickHider.Instance.hideOtherNames.get() && !instance.equals(MinecraftClient.getInstance().player)){
                return new LiteralText(NickHider.Instance.hiddenNameOthers.get());
            }
        }
        return instance.method_6344();
    }

    @Inject(method = "method_10208(Lnet/minecraft/entity/LivingEntity;DDD)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;III)I"))
    public void addBadge(LivingEntity livingEntity, double d, double e, double f, CallbackInfo ci){
        if(!NickHider.Instance.hideOwnName.get() && livingEntity.equals(MinecraftClient.getInstance().player))
            AxolotlClient.addBadge(livingEntity);
        else if (!NickHider.Instance.hideOtherNames.get()  && !livingEntity.equals(MinecraftClient.getInstance().player))
            AxolotlClient.addBadge(livingEntity);
    }

}
