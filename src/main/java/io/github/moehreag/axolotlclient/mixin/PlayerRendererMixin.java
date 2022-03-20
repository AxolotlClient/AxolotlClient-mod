package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntityRenderer.class)
public class PlayerRendererMixin {

    @ModifyArgs(method = "method_10209(Lnet/minecraft/client/network/AbstractClientPlayerEntity;DDDLjava/lang/String;FD)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;method_10209(Lnet/minecraft/entity/Entity;DDDLjava/lang/String;FD)V"))
    public void addBadge(Args args){
        if(Axolotlclient.CONFIG != null) {

            LiteralText badgesText = (LiteralText) new LiteralText(Axolotlclient.badge).append(Axolotlclient.CONFIG.NickHider.hideOtherNames ? new LiteralText(Axolotlclient.CONFIG.NickHider.otherName) : args.get(1));
            //badgesText.setStyle(.withExclusiveFormatting(Formatting.WHITE).withFont(Axolotlclient.FONT));
            badgesText.setStyle(new Style().setFormatting(Formatting.WHITE));

            AbstractClientPlayerEntity player = args.get(0);

            if (Axolotlclient.features && Axolotlclient.CONFIG.badgeOptions.showBadge && Axolotlclient.isUsingClient(player.getUuid())) {
                if (player.getUuid() == (MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getUuid() : null)) {

                    args.set(1, new LiteralText(Axolotlclient.CONFIG.badgeOptions.CustomBadge ? Axolotlclient.CONFIG.badgeOptions.badgeText + " " : Axolotlclient.badge + " ").append(Axolotlclient.CONFIG.NickHider.hideOwnName ? new LiteralText(Axolotlclient.CONFIG.NickHider.OwnName) : args.get(1)));
                } else {
                    args.set(1, (Axolotlclient.CONFIG.badgeOptions.CustomBadge ? new LiteralText(Axolotlclient.CONFIG.badgeOptions.badgeText + " ").append(Axolotlclient.CONFIG.NickHider.hideOtherNames ? new LiteralText(Axolotlclient.CONFIG.NickHider.otherName) : args.get(1)) : badgesText));
                }
                return;
            }
            if (Axolotlclient.CONFIG.NickHider.hideOtherNames && Axolotlclient.features) {
                assert MinecraftClient.getInstance().player != null;
                if (player.getName() != MinecraftClient.getInstance().player.getName()) {
                    args.set(1, new LiteralText(Axolotlclient.CONFIG.NickHider.otherName));
                }
            }
        }
    }
}
