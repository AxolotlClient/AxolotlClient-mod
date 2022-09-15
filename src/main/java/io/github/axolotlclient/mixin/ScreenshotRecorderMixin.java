package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.framebuffer.Framebuffer;
import com.mojang.blaze3d.texture.NativeImage;
import io.github.axolotlclient.modules.screenshotUtils.ScreenshotUtils;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;
import java.util.function.Consumer;

@Mixin(ScreenshotRecorder.class)
public abstract class ScreenshotRecorderMixin {

    @Shadow @Final private static Logger LOGGER;
    private static File currentFile;
    private static NativeImage image;

    @Inject(method = "saveScreenshotInner", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getIoWorkerExecutor()Ljava/util/concurrent/ExecutorService;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void getImageFile(File gameDirectory, @Nullable String fileName, Framebuffer framebuffer, Consumer<Text> messageReceiver, CallbackInfo ci, NativeImage nativeImage, File file, File file2){
        currentFile = file2;
        image = nativeImage;
    }

    // Yes. This is not nice. If you know a better way t achieve this please lmk!
    @Inject(method = "saveScreenshotInner", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ExecutorService;execute(Ljava/lang/Runnable;)V"), cancellable = true)
    private static void onScreenshotSaveSuccess(File gameDirectory, @Nullable String fileName, Framebuffer framebuffer, Consumer<Text> messageReceiver, CallbackInfo ci){
        Util.getIoWorkerExecutor().execute(() -> {
            try {
                image.writeFile(currentFile);
                Text text = Text.literal(currentFile.getName())
                        .formatted(Formatting.UNDERLINE)
                        .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, currentFile.getAbsolutePath())));
                messageReceiver.accept(ScreenshotUtils.getInstance().onScreenshotTaken(Text.translatable("screenshot.success", text), currentFile));
            } catch (Exception var7) {
                LOGGER.warn("Couldn't save screenshot", var7);
                messageReceiver.accept(Text.translatable("screenshot.failure", var7.getMessage()));
            } finally {
                image.close();
            }
        });
        ci.cancel();
    }
}
