package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

	protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@Inject(method = "hasLabel*", at = @At("HEAD"), cancellable = true)
	private void showOwnNametag(T livingEntity, CallbackInfoReturnable<Boolean> cir){
		if (AxolotlClient.CONFIG.showOwnNametag.get() && livingEntity == MinecraftClient.getInstance().player) {
			cir.setReturnValue(true);
		}
	}
}
