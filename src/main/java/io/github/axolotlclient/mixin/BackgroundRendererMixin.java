package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.modules.sky.SkyboxManager;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {

    @Inject(method = "applyFog", at = @At("TAIL"))
    private static void applyNoFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci){
        if (camera.getSubmersionType() == CameraSubmersionType.NONE && (thickFog || fogType == BackgroundRenderer.FogType.FOG_TERRAIN)) {
            if (SkyboxManager.getInstance().hasSkyBoxes()) {
                RenderSystem.setShaderFogStart(Short.MAX_VALUE - 1);
                RenderSystem.setShaderFogEnd(Short.MAX_VALUE);
            }
        }
    }
}
