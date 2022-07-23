package io.github.axolotlclient.mixin;

import io.github.axolotlclient.NetworkHelper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Inject(method = "onGameJoin", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;yaw:F"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci){
        NetworkHelper.setOnline();
    }

}
