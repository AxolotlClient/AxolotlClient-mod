package io.github.axolotlclient.mixin;

import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {

    @Accessor
    int getStarsList();

    @Accessor
    VertexBuffer getStarsBuffer();

    @Accessor
    boolean getVbo();
}
