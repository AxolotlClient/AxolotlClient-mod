package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity> {
    public PlayerRendererMixin(EntityRenderDispatcher dispatcher, EntityModel model, float shadowSize) {
        super(dispatcher, model, shadowSize);
    }


    @ModifyArgs(method = "method_10209(Lnet/minecraft/client/network/AbstractClientPlayerEntity;DDDLjava/lang/String;FD)V",
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
    }
}
