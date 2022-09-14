package io.github.axolotlclient.mixin;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(ScreenshotUtils.class)
public abstract class ScreenshotUtilsMixin {

    private static File currentFile;

    @Redirect(method = "saveScreenshot(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/gl/Framebuffer;)Lnet/minecraft/text/Text;", at = @At(value = "INVOKE", target = "Ljava/io/File;getName()Ljava/lang/String;"))
    private static String getImageFile(File instance){
        currentFile = instance;
        return instance.getName();
    }

    @Inject(method = "saveScreenshot(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/gl/Framebuffer;)Lnet/minecraft/text/Text;", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private static void onScreenshotSaveSuccess(File parent, String name, int textureWidth, int textureHeight, Framebuffer buffer, CallbackInfoReturnable<Text> cir){
        cir.setReturnValue(io.github.axolotlclient.modules.screenshotUtils.
                ScreenshotUtils.getInstance().onScreenshotTaken(cir.getReturnValue(), currentFile));
    }
}
