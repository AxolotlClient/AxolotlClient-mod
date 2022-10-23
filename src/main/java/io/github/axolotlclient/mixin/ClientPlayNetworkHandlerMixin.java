package io.github.axolotlclient.mixin;

import io.github.axolotlclient.NetworkHelper;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.simple.TPSHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onGameJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;setYaw(F)V"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci){
        NetworkHelper.setOnline();
    }

    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"))
    private void onWorldUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        TPSHud tpsHud = (TPSHud) HudManager.getInstance().get(TPSHud.ID);
        tpsHud.updateTime(packet.getTime());
    }

}
