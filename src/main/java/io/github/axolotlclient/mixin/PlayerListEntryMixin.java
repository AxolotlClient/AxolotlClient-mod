package io.github.axolotlclient.mixin;

import com.mojang.authlib.GameProfile;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {

    @Shadow @Final private GameProfile profile;

    @Inject(method = "getSkinTexture", at = @At("RETURN"), cancellable = true)
    public void hideSkins(CallbackInfoReturnable<Identifier> cir){
        if(profile.equals(MinecraftClient.getInstance().player.getGameProfile()) &&
                NickHider.Instance.hideOwnSkin.get()){

            cir.setReturnValue(DefaultSkinHelper.getTexture(profile.getId()));
        } else if(!profile.equals(MinecraftClient.getInstance().player.getGameProfile()) &&
                NickHider.Instance.hideOtherSkins.get()){

            cir.setReturnValue(DefaultSkinHelper.getTexture(profile.getId()));
        }
    }
}
