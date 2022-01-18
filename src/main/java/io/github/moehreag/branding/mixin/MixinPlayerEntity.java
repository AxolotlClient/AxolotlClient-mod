package io.github.moehreag.branding.mixin;

import com.mojang.authlib.GameProfile;
import io.github.moehreag.branding.Axolotlclient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {

	@Shadow @Final private GameProfile gameProfile;


	@Inject(method = "getName", at = @At("RETURN"), cancellable = true)
	private void addBadge(CallbackInfoReturnable<Text> cir) {
		try {
			if (Axolotlclient.CONFIG.showBadge && Axolotlclient.isUsingClient(this.gameProfile.getId())) {
				cir.setReturnValue(new LiteralText("✵ " + this.gameProfile.getName()));
				cir.cancel();
			}
		} catch (Exception e){cir.setReturnValue(new LiteralText(this.gameProfile.getName()));}
	}


}
