package io.github.moehreag.axolotlclient.mixin;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.net.URL;
import java.util.Map;

@Mixin(SoundSystem.class)
public interface AccessorSoundSystem {

    @Invoker
    URL invokeMethod_7096(Identifier id);

    @Accessor
    Map<SoundInstance, String> getField_8196();
}
