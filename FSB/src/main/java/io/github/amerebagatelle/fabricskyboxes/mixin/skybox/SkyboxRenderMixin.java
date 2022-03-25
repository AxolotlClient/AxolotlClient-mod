package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class SkyboxRenderMixin {
    /**
     * Contains the logic for when skyboxes should be rendered.
     */
    @Inject(method = "method_9891", at = @At("HEAD"), cancellable = true)
    private void renderCustomSkyboxes(float f, int i, CallbackInfo ci) {
        float total = SkyboxManager.getInstance().getTotalAlpha();
        SkyboxManager.getInstance().renderSkyboxes();
        if (total > SkyboxManager.MINIMUM_ALPHA) {
            ci.cancel();
        }
    }
}
