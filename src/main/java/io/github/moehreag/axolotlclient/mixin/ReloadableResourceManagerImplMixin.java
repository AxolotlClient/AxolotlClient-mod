package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.modules.hud.HudManager;
import io.github.moehreag.axolotlclient.modules.hud.gui.hud.PackDisplayHud;
import io.github.moehreag.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import io.github.moehreag.axolotlclient.modules.sky.SkyResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ReloadableResourceManagerImpl.class)
public class ReloadableResourceManagerImplMixin {

    @Inject(method = "reload", at=@At("TAIL"))
    public void loadSkies(List<ResourcePack> resourcePacks, CallbackInfo ci){
        HypixelAbstractionLayer.clearPlayerData();
        if(AxolotlClient.initalized)SkyResourceManager.reload(resourcePacks);
        else{SkyResourceManager.packs=resourcePacks;}

        PackDisplayHud hud = (PackDisplayHud) HudManager.getINSTANCE().get(PackDisplayHud.ID);
        if(hud.isEnabled()){
            hud.widgets.clear();
        }

        AxolotlClient.packs=resourcePacks;
    }

    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    public void getResource(Identifier id, CallbackInfoReturnable<Resource> cir){
        if(AxolotlClient.runtimeResources.get(id) != null){
            cir.setReturnValue(AxolotlClient.runtimeResources.get(id));
        }
    }

}
