package net.logandark.branding.mixin;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	/**
	 * @author LoganDark
	 */
	@Overwrite
	public boolean isModded() {
		return false;
	}

	@Redirect(
		method = "method_1509", // "Is Modded" lambda in addSystemDetailsToCrashReport
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Class;getSigners()[Ljava/lang/Object;"
		),
		remap = false
	)
	private static Object[] onGetSigners(Class aClass) {
		return new Object[0]; // not null
	}

	@Redirect(
		method = "<init>",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/RunArgs$Game;version:Ljava/lang/String;"
		)
	)
	private String redirectVersion(RunArgs.Game game) {
		//String version = game.version;

		//if (version.equals("Fabric")) {
		return SharedConstants.getGameVersion().getName();
		//}

		//return version;
	}

	@Redirect(
		method = "<init>",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/RunArgs$Game;versionType:Ljava/lang/String;"
		)
	)
	private String redirectVersionType(RunArgs.Game game) {
		String versionType = game.versionType;

		if (versionType.endsWith("Fabric")) {
			if (versionType.endsWith("/Fabric")) {
				return versionType.substring(0, versionType.length() - 7);
			}

			return "release";
		}

		return versionType;
	}
}
