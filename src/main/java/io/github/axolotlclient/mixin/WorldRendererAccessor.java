package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {

    @Accessor
    VertexBuffer getStarsBuffer();

}
