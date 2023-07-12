package io.github.axolotlclient.mixin;

import java.nio.ByteBuffer;

import net.minecraft.client.render.VertexBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VertexBuffer.class)
public class VertexBufferMixin {
	@Shadow
	private int id;

	@Inject(method = "data", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexBuffer;bind()V"), cancellable = true)
	private void axolotlclient$ignoreDeletedBuffers(ByteBuffer byteBuffer, CallbackInfo ci) {
		if (id == -1) {
			ci.cancel();
		}
	}
}
