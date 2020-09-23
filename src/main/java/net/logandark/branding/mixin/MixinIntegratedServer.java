package net.logandark.branding.mixin;

import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IntegratedServer.class)
public class MixinIntegratedServer {
	@Redirect(
		method = "getModdedStatusMessage",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Class;getSigners()[Ljava/lang/Object;"
		)
	)
	private Object[] onGetSigners(Class aClass) {
		return new Object[0]; // not null
	}
}
