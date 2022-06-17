package io.github.axolotlclient.mixin;

import io.github.axolotlclient.util.Util;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(TextureManager.class)
public class TextureManagerMixin {

    @Shadow @Final private Map<Identifier, Texture> textures;

    @Inject(method = "tick", at = @At("TAIL"))
    public void getTextures(CallbackInfo ci){
        Util.setTextures(textures);
    }
}
