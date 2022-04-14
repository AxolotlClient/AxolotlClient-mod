package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.modules.hud.HudManager;
import io.github.moehreag.axolotlclient.modules.hud.gui.hud.CrosshairHud;
import io.github.moehreag.axolotlclient.modules.hud.gui.hud.ScoreboardHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.Window;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow protected abstract boolean method_9429();

    @Inject(method = "renderScoreboardObjective", at = @At("HEAD"), cancellable = true)
    public void customScoreBoard(ScoreboardObjective objective, Window window, CallbackInfo ci){
        ScoreboardHud hud = (ScoreboardHud) HudManager.getINSTANCE().get(ScoreboardHud.ID);
        if(hud.isEnabled()){
            ci.cancel();
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;method_9429()Z"))
    public boolean noCrosshair(InGameHud instance){
        CrosshairHud hud = (CrosshairHud) HudManager.getINSTANCE().get(CrosshairHud.ID);
        if(hud.isEnabled()) return false;
        return method_9429();
    }
}
