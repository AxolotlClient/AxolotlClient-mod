package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.PackDisplayHud;
import io.github.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import io.github.axolotlclient.modules.sky.SkyResourceManager;
import io.github.axolotlclient.modules.sky.SkyboxManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.pack.ResourcePack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManager.class)
public class MixinReloadableResourceManager {

	@Inject(method = "reload", at = @At("HEAD"))
	public void reloadStart(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReload> cir){
		SkyboxManager.getInstance().clearSkyboxes();
	}

    @Inject(method = "reload", at=@At("TAIL"))
    public void loadSkies(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> resourcePacks, CallbackInfoReturnable<ResourceReload> cir){
        HypixelAbstractionLayer.clearPlayerData();
	    SkyResourceManager.reload(resourcePacks);

	    AxolotlClient.packs=resourcePacks;

        PackDisplayHud hud = (PackDisplayHud) HudManager.getINSTANCE().get(PackDisplayHud.ID);
        if(hud.isEnabled()){
            hud.widgets.clear();
			hud.init();
        }


    }

    @Inject(method = "method_14486", at = @At("HEAD"), cancellable = true)
    public void getResource(Identifier id, CallbackInfoReturnable<Optional<Resource>> cir){
        if(AxolotlClient.runtimeResources.get(id) != null){
            cir.setReturnValue(Optional.of(AxolotlClient.runtimeResources.get(id)));
        }
    }

}
