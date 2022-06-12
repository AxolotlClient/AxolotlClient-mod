package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.NetworkHelper;
import io.github.moehreag.axolotlclient.config.Color;
import io.github.moehreag.axolotlclient.modules.hud.HudManager;
import io.github.moehreag.axolotlclient.modules.hud.gui.hud.CPSHud;
import io.github.moehreag.axolotlclient.util.DiscordRPC;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {


    @Shadow @Final private String gameVersion;

    @Shadow private TextureManager textureManager;

    /**
     * @author meohreag
     * @reason Customize Window title for use in AxolotlClient
     */
    /*@Inject(method = "setPixelFormat", at = @At("TAIL"))
    public void setWindowTitle(CallbackInfo ci){
        Display.setTitle("AxolotlClient "+ this.gameVersion);
    }

    @Redirect(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;loadLogo(Lnet/minecraft/client/texture/TextureManager;)V"))
    public void noLogo(MinecraftClient instance, TextureManager textureManager){}

    @Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/Achievement;setStatFormatter(Lnet/minecraft/stat/StatFormatter;)Lnet/minecraft/advancement/Achievement;"))
    public void loadSkiesOnStartup(CallbackInfo ci){
        SkyResourceManager.onStartup();
        try {
            this.loadLogo(this.textureManager);
        } catch (Exception ignored){}
    }

    //Don't ask me why we need both here, but otherwise it looks ugly
    @Redirect(method = "loadLogo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;color(IIII)Lnet/minecraft/client/render/BufferBuilder;"))
    public BufferBuilder loadingScreenColor(BufferBuilder instance, int red, int green, int blue, int alpha){

        return instance.color(AxolotlClient.CONFIG.loadingScreenColor.get().getRed(),
                AxolotlClient.CONFIG.loadingScreenColor.get().getGreen(),
                AxolotlClient.CONFIG.loadingScreenColor.get().getBlue(), AxolotlClient.CONFIG.loadingScreenColor.get().getAlpha());
    }

    @Redirect(method = "method_9382", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;color(IIII)Lnet/minecraft/client/render/BufferBuilder;"))
    public BufferBuilder loadingScreenBg(BufferBuilder instance, int red, int green, int blue, int alpha){

        return instance.color(AxolotlClient.CONFIG.loadingScreenColor.get().getRed(),
                AxolotlClient.CONFIG.loadingScreenColor.get().getGreen(),
                AxolotlClient.CONFIG.loadingScreenColor.get().getBlue(), AxolotlClient.CONFIG.loadingScreenColor.get().getAlpha());
    }*/

    /*@Redirect(
            method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/RunArgs$Game;version:Ljava/lang/String;"))
    private String redirectVersion(RunArgs.Game game) {
        return "1.8.9";
    }*/

    @Inject(method = "<init>", at = @At("HEAD"))
    private static void startup(RunArgs runArgs, CallbackInfo ci){
        DiscordRPC.startup();
    }

    @Inject(method = "stop", at = @At("HEAD"))
    public void stop(CallbackInfo ci){
        NetworkHelper.setOffline();
        DiscordRPC.shutdown();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickClient(CallbackInfo ci) {
	    Color.tickChroma();

	    AxolotlClient.TickClient();
	    DiscordRPC.update();
    }

    @Inject(method = "startOnlineMode", at = @At(value = "HEAD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;method_1236(Lnet/minecraft/entity/player/PlayerEntity;)V"))
    public void login(CallbackInfo ci){
        NetworkHelper.setOnline();
    }

}
