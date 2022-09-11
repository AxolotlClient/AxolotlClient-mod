package io.github.axolotlclient.mixin;

import net.minecraft.util.ModStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ModStatus.class)
public abstract class ModStatusMixin {

	/**
	 * @author moehreag
	 * @reason Remove mod signs because they're ugly
	 */
	@Overwrite
	public boolean isModded(){
		return false;
	}
}
