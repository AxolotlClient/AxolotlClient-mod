package io.github.moehreag.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.util.Util;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlStateManager.class)
public abstract class GlStateManagerMixin {

    @Inject(method = "color4f", at = @At("HEAD"), cancellable = true)
    private static void nightMode(float red, float green, float blue, float alpha, CallbackInfo ci){
        if(Axolotlclient.CONFIG.nightMode.get()){

            if (red != Util.GlColor.red || green != Util.GlColor.green || blue != Util.GlColor.blue || alpha != Util.GlColor.alpha) {
                Util.GlColor.red = red;
                Util.GlColor.green = green;
                Util.GlColor.blue = blue;
                Util.GlColor.alpha = alpha;

                GL11.glColor4f(red, green, blue / 2, alpha);
            }

            ci.cancel();
        }
    }
}
