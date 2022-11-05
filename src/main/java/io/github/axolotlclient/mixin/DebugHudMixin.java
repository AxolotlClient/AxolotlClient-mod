package io.github.axolotlclient.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin {

    @Redirect(method = "getLeftText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ClientBrandRetriever;getClientModName()Ljava/lang/String;"))
    public String nicerVersionString(){
        if(FabricLoader.getInstance().getModContainer("axolotlclient").isPresent()) {
            return ClientBrandRetriever.getClientModName() + "/" + FabricLoader.getInstance().getModContainer("axolotlclient").get().getMetadata().getVersion().getFriendlyString();
        }
        return ClientBrandRetriever.getClientModName();
    }
}
