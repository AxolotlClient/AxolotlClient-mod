package io.github.moehreag.branding.mixin;


import io.github.moehreag.branding.Axolotlclient;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrashReport.class)
public class MixinCrashReport {

	@Inject(method = "addStackTrace", at = @At(value = "TAIL"))
	public void addAxolotlclientInfo(StringBuilder builder, CallbackInfo ci){
			builder.append("\n\n");
			builder.append("---- Axolotlclient Information ----\n");
			if (Axolotlclient.TitleDisclaimer){
				builder.append("Unsupported Mods were found!\n");
				builder.append("Suspected mod: ").append(Axolotlclient.badmod);
			} else {
				builder.append("No unsupported Mods found…\n");
				builder.append("No clear sign why his crashed…");
			}

			builder.append("\n\n");
	}
}
