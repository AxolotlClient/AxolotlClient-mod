package io.github.moehreag.axolotlclient.mixin;

import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.obfuscate.DontObfuscate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ClientBrandRetriever.class)
public abstract class MixinClientBrandRetriever {
	/**
	 * @author moehreag
	 * @reason Modify client brand
	 */
	@Overwrite(remap = false)
	@DontObfuscate
	public static String getClientModName() {
		return "Axolotlclient";
	}
}
