/*
 * Copyright © 2024 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.blur.MenuBlur;
import io.github.axolotlclient.modules.blur.MotionBlur;
import io.github.axolotlclient.modules.zoom.Zoom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@Final
	@Shadow
	private MinecraftClient client;

	@Shadow
	private boolean renderingPanorama;

	@WrapOperation(method = "getFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
	private float disableDynamicFov(float delta, float start, float end, Operation<Float> original) {
		if (!AxolotlClient.CONFIG.dynamicFOV.get()) {
			return 1.0f;
		}
		return original.call(delta, start, end);
	}

	@WrapMethod(method = "getFov")
	private double getFov(Camera camera, float partialTick, boolean useFovSetting, Operation<Double> original) {
		if (this.renderingPanorama) {
			return original.call(camera, partialTick, useFovSetting);
		}
		Zoom.update();
		return Zoom.getFov(original.call(camera, partialTick, useFovSetting), partialTick);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getFramebuffer()Lnet/minecraft/client/gl/Framebuffer;"))
	public void axolotlclient$worldMotionBlur(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
		MenuBlur.getInstance().updateBlur();
		axolotlclient$motionBlur(tickDelta, startTime, tick, null);
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void axolotlclient$motionBlur(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
		if (ci != null && !MotionBlur.getInstance().inGuis.get()) {
			return;
		}

		this.client.getProfiler().push("Motion Blur");

		if (MotionBlur.getInstance().enabled.get()) {
			MotionBlur blur = MotionBlur.getInstance();
			blur.onUpdate();
			blur.shader.render(tickDelta);
			RenderSystem.enableTexture();
		}

		this.client.getProfiler().pop();
	}

	@Inject(method = "bobView", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"), cancellable = true)
	private void axolotlclient$minimalViewBob(MatrixStack matrixStack, float f, CallbackInfo ci, @Local(ordinal = 2) float h, @Local(ordinal = 3) float i) {
		if (AxolotlClient.CONFIG.minimalViewBob.get()) {
			h /= 2;
			i /= 2;
			matrixStack.translate(MathHelper.sin(h * (float) Math.PI) * i * 0.5F, -Math.abs(MathHelper.cos(h * (float) Math.PI) * i), 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.sin(h * (float) Math.PI) * i * 3.0F));
			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(Math.abs(MathHelper.cos(h * (float) Math.PI - 0.2F) * i) * 5.0F));
			ci.cancel();
		}
	}

	@Inject(method = "bobViewWhenHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getCameraEntity()Lnet/minecraft/entity/Entity;", ordinal = 1), cancellable = true)
	private void axolotlclient$noHurtCam(MatrixStack matrixStack, float f, CallbackInfo ci) {
		if (AxolotlClient.CONFIG.noHurtCam.get()) {
			ci.cancel();
		}
	}
}
