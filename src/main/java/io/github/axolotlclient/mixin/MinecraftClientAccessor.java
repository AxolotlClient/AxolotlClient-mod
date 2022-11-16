package io.github.axolotlclient.mixin;

import lombok.experimental.Accessors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.ClientTickTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {

    @Accessor
    ClientTickTracker getTicker();

    @Accessor
    String getServerAddress();

    @Accessor
    int getServerPort();
}
