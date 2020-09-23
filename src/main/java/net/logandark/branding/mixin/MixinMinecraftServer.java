package net.logandark.branding.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
	/**
	 * @author LoganDark
	 */
	@Overwrite
	public String getServerModName() {
		return "vanilla";
	}
}
