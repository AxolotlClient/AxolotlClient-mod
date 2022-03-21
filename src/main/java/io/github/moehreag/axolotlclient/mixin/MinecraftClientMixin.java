package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.NetworkHelper;
import io.github.moehreag.axolotlclient.util.DiscordRPC;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.util.Identifier;
import net.minecraft.world.level.LevelInfo;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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
        DiscordRPC.startup();
    }


    /*@ModifyArg(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;<init>(Lnet/minecraft/client/options/GameOptions;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/texture/TextureManager;Z)V"), index = 1)
    public Identifier modifyFontTexture(Identifier fontTexture){
        return Axolotlclient.FONT;
    }*/




    @Inject(method = "stop", at = @At("HEAD"))
    public void stop(CallbackInfo ci){
        if (Axolotlclient.features) NetworkHelper.setOffline();
        DiscordRPC.shutdown();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickClient(CallbackInfo ci){
        DiscordRPC.update();
    }

}
