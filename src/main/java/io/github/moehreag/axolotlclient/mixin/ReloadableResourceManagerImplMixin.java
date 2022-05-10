package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.modules.hud.HudManager;
import io.github.moehreag.axolotlclient.modules.hud.gui.hud.PackDisplayHud;
import io.github.moehreag.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import io.github.moehreag.axolotlclient.modules.sky.SkyResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ReloadableResourceManagerImpl.class)
public class ReloadableResourceManagerImplMixin {

    @Inject(method = "reload", at=@At("TAIL"))
    public void loadSkies(List<ResourcePack> resourcePacks, CallbackInfo ci){
        HypixelAbstractionLayer.clearPlayerData();
        if(Axolotlclient.initalized)SkyResourceManager.reload(resourcePacks);
        else{SkyResourceManager.packs=resourcePacks;}

        PackDisplayHud hud = (PackDisplayHud) HudManager.getINSTANCE().get(PackDisplayHud.ID);
        if(hud.isEnabled()){
            hud.widgets.clear();
        }

        Axolotlclient.packs=resourcePacks;
    }
}
