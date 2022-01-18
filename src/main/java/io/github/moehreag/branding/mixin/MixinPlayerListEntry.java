package io.github.moehreag.branding.mixin;

import com.mojang.authlib.GameProfile;
import io.github.moehreag.branding.Axolotlclient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public abstract class MixinPlayerListEntry {

	@Final
	@Shadow private GameProfile profile;

	@Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
	private void addIcon(CallbackInfoReturnable<Text> cir){

		if (Axolotlclient.CONFIG.showBadge && Axolotlclient.isUsingClient(this.profile.getId())){
			cir.setReturnValue(new LiteralText("✵ "+ this.profile.getName()));
		cir.cancel();
		}
	}
}