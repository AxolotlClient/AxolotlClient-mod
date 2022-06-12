package io.github.moehreag.axolotlclient.mixin;

//@Mixin(ReloadableResourceManagerImpl.class)
public class ReloadableResourceManagerImplMixin {

    /*@Inject(method = "reload", at=@At("TAIL"))
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
    }*/

}
