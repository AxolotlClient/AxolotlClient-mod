package io.github.moehreag.axolotlclient.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    /*@Shadow protected abstract boolean method_9429();

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
        if(hud.isEnabled()) {
            GlStateManager.blendFuncSeparate(775, 769, 1, 0);
            GlStateManager.enableAlphaTest();
            return false;
        }
        return method_9429();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;III)I", ordinal = 0))
    public int actionBar(TextRenderer instance, String text, int x, int y, int color){
        ActionBarHud hud = (ActionBarHud) HudManager.getINSTANCE().get(ActionBarHud.ID);
        if(hud.isEnabled()){
            hud.setActionBar(text, color);
            return 0;
        }
        return instance.draw(text, x, y, color);
    }

    @Inject(method = "renderBossBar", at = @At("HEAD"), cancellable = true)
    public void CustomBossBar(CallbackInfo ci){
        BossBarHud hud = (BossBarHud) HudManager.getINSTANCE().get(BossBarHud.ID);
        if(hud.isEnabled()){
            ci.cancel();
        }
    }*/
}
