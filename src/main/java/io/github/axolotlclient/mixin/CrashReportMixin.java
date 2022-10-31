package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrashReport.class)
public abstract class CrashReportMixin {

	@Inject(method = "addStackTrace", at = @At(value = "TAIL"))
	public void addAxolotlclientInfo(StringBuilder builder, CallbackInfo ci){
		if (AxolotlClient.badmod != null) {
			builder.append("\n\n")
					.append("---- Axolotlclient Information ----\n");
			builder.append("Unsupported Mods were found!\n")
					.append("Suspected mod: ")
					.append(AxolotlClient.badmod.name());
			builder.append("\n\n");
		}
	}
}