package io.github.moehreag.axolotlclient.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface AccessorMinecraftClient {
	@Accessor
	static int getCurrentFps() {
		return 0;
	}
}
