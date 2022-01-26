package io.github.moehreag.axolotlclient.mixin;


import io.github.moehreag.axolotlclient.Axolotlclient;
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
			} else if (!Axolotlclient.features) {
				builder.append("The mod is being used outside of Axolotlclient!\n");
				builder.append("No support whatsoever will be given!");

			} else {
				builder.append("No unsupported Mods found…\n");
				builder.append("No clear sign why his crashed…");
			}

			builder.append("\n\n");
	}
}
