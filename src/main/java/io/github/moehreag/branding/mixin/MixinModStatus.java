package io.github.moehreag.branding.mixin;

import net.minecraft.util.ModStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ModStatus.class)
public class MixinModStatus {

	/**
	 * @author moehreag
	 */
	@Overwrite
	public boolean isModded(){
		return false;
	}
}
