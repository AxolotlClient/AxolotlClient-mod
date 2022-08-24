package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.CrosshairHud;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHud.class)
public abstract class MixinDebugHud {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
    public void onRender(MatrixStack matrices, CallbackInfo ci){

        CrosshairHud hud = (CrosshairHud) HudManager.getInstance().get(CrosshairHud.ID);
        if(hud.isEnabled() && hud.showInF3.get()){
            hud.render(matrices);
        }

    }
}
