package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.sky.SkyboxManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.dimension.Dimension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This implementation of custom skies is based on the FabricSkyBoxes mod by AMereBagatelle
 * <a href="https://github.com/AMereBagatelle/FabricSkyBoxes">Github Link.</a>
 * @license MIT
 **/

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow private ClientWorld world;

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    public void renderCustomSky(float tickDelta, int anaglyphFilter, CallbackInfo ci){
        if(this.world.dimension.canPlayersSleep()){
            if(AxolotlClient.CONFIG.customSky.get() && SkyboxManager.getInstance().hasSkyBoxes()) {
                GlStateManager.depthMask(false);
                this.client.profiler.push("Custom Skies");
                SkyboxManager.getInstance().renderSkyboxes(tickDelta, world.getRainGradient(tickDelta));
                this.client.profiler.pop();
                GlStateManager.depthMask(true);
                ci.cancel();
            }
        }
    }

    @Redirect(method = "renderClouds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/dimension/Dimension;getCloudHeight()F"))
    public float getCloudHeight(Dimension instance){
        return AxolotlClient.CONFIG.cloudHeight.get();
    }


    @ModifyArg(method = "drawBlockOutline", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glLineWidth(F)V"), remap = false)
    public float OutlineWidth(float width){
        if(AxolotlClient.CONFIG.enableCustomOutlines.get() && AxolotlClient.CONFIG.outlineWidth.get()>1){
            return 1.0F+ AxolotlClient.CONFIG.outlineWidth.get();
        }
        return width;
    }

    @Inject(method = "drawBlockOutline", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;color4f(FFFF)V", shift = At.Shift.AFTER))
    public void customOutlineColor(PlayerEntity playerEntity, BlockHitResult blockHitResult, int i, float f, CallbackInfo ci){
        if(AxolotlClient.CONFIG.enableCustomOutlines.get()){
            GlStateManager.clearColor();

            int color = AxolotlClient.CONFIG.outlineColor.get().getAsInt();
            float a = (float)(color >> 24 & 0xFF) / 255.0F;
            float r = (float)(color >> 16 & 0xFF) / 255.0F;
            float g = (float)(color >> 8 & 0xFF) / 255.0F;
            float b = (float)(color & 0xFF) / 255.0F;
            GlStateManager.color4f(r,g,b,a);
        }
    }
}
