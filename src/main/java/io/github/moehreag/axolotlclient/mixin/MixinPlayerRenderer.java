package io.github.moehreag.axolotlclient.mixin;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerRenderer {


    /*@ModifyArgs(method = "method_10209(Lnet/minecraft/client/network/AbstractClientPlayerEntity;DDDLjava/lang/String;FD)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;method_10209(Lnet/minecraft/entity/Entity;DDDLjava/lang/String;FD)V"))
    public void modifiyName(Args args){
        if(AxolotlClient.CONFIG != null) {
            AbstractClientPlayerEntity player = args.get(0);
            if(player.getUuid()==MinecraftClient.getInstance().player.getUuid() &&
                    NickHider.Instance.hideOwnName.get()){
                args.set(4, NickHider.Instance.hiddenNameSelf.get());
            } else if(player.getUuid()!=MinecraftClient.getInstance().player.getUuid() &&
                    NickHider.Instance.hideOtherNames.get()){
                args.set(4, NickHider.Instance.hiddenNameOthers.get());
            }
        }
    }*/
}
