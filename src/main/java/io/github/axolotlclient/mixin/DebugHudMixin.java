package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.CrosshairHud;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHud.class)
public class DebugHudMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
    public void onRender(Window window, CallbackInfo ci){

        CrosshairHud hud = (CrosshairHud) HudManager.getINSTANCE().get(CrosshairHud.ID);
        if(hud.isEnabled() && hud.showInF3.get()){
            hud.render();
        }

    }

    @Redirect(method = "getLeftText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ClientBrandRetriever;getClientModName()Ljava/lang/String;"))
    public String nicerVersionString(){
        if(FabricLoader.getInstance().getModContainer("axolotlclient").isPresent()) {
            return ClientBrandRetriever.getClientModName() + "/" + FabricLoader.getInstance().getModContainer("axolotlclient").get().getMetadata().getVersion().getFriendlyString();
        }
        return ClientBrandRetriever.getClientModName();
    }
}
