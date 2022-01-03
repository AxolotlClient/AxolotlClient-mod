package io.github.moehreag.branding.mixin;

import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
	/**
	 * @author LoganDark
	 */
	@Overwrite(remap = false)
	@DontObfuscate
	public String getServerModName() {
		return "vanilla";
	}
}
