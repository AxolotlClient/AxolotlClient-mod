package io.github.axolotlclient.mixin;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SoundSystem.class)
public interface AccessorSoundSystem {

    @Accessor
    Map<SoundInstance, String> getField_8196();
}
