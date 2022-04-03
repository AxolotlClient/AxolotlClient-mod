package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.NetworkHelper;
import io.github.moehreag.axolotlclient.util.DiscordRPC;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.world.level.LevelInfo;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {


    @Shadow @Final private String gameVersion;

    /**
     * @author meohreag
     * @reason Customize Window title for use in AxolotlClient
     */
    @Inject(method = "setPixelFormat", at = @At("TAIL"))
    public void setWindowTitle(CallbackInfo ci){
        Display.setTitle("Axolotlclient "+ this.gameVersion);
    }

    @Redirect(
            method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/RunArgs$Game;version:Ljava/lang/String;"))
    private String redirectVersion(RunArgs.Game game) {
        return "1.8.9";
    }

    @Inject(method = "startGame", at = @At("HEAD"))
    public void startup(String worldName, String string, LevelInfo levelInfo, CallbackInfo ci){
        NetworkHelper.setOnline();
        DiscordRPC.startup();
    }

    @Inject(method = "stop", at = @At("HEAD"))
    public void stop(CallbackInfo ci){
        NetworkHelper.setOffline();
        DiscordRPC.shutdown();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickClient(CallbackInfo ci){
        Axolotlclient.TickClient();
        DiscordRPC.update();
    }

    @Inject(method = "initializeGame", at = @At("TAIL"))
    public void onLaunch(CallbackInfo ci){
        NetworkHelper.setOnline();
    }

}
