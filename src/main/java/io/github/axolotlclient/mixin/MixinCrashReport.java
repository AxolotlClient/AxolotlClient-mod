package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(CrashReport.class)
public class MixinCrashReport {

	@Inject(method = "addStackTrace", at = @At(value = "TAIL"))
	public void addAxolotlclientInfo(StringBuilder builder, CallbackInfo ci){
			builder.append("\n\n")
				.append("---- Axolotlclient Information ----\n");
			if (!Objects.equals(AxolotlClient.badmod, "")){
				builder.append("Unsupported Mods were found!\n")
					.append("Suspected mod: ")
					.append(AxolotlClient.badmod);
			}
			builder.append("\n\n");
	}
}
