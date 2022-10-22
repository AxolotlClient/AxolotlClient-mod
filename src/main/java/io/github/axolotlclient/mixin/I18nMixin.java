package io.github.axolotlclient.mixin;

import io.github.axolotlclient.util.translation.TranslationProvider;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(I18n.class)
public abstract class I18nMixin {

    private static final String KEY_PREFIX = "axolotlclient.";

    @Inject(method = "translate", at = @At("HEAD"), cancellable = true)
    private static void translate(String key, Object[] args, CallbackInfoReturnable<String> callback) {
        if(key.startsWith(KEY_PREFIX)) {
            callback.setReturnValue(TranslationProvider.format(TranslationProvider.translate(key.substring(KEY_PREFIX.length())), args));
        }
    }
}
