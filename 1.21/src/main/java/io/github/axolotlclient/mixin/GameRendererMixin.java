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
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.blur.MotionBlur;
import io.github.axolotlclient.modules.zoom.Zoom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DeltaTracker;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Axis;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@Final
	@Shadow
	MinecraftClient client;

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

	@Inject(method = "render",
		at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/framebuffer/Framebuffer;beginWrite(Z)V"))
	public void axolotlclient$worldMotionBlur(DeltaTracker tracker, boolean tick, CallbackInfo ci) {
		axolotlclient$motionBlur(tracker, tick, null);
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void axolotlclient$motionBlur(DeltaTracker tracker, boolean tick, CallbackInfo ci) {
		if (ci != null && !MotionBlur.getInstance().inGuis.get()) {
			return;
		}

		this.client.getProfiler().push("Motion Blur");

		if (MotionBlur.getInstance().enabled.get()) {
			MotionBlur blur = MotionBlur.getInstance();
			blur.onUpdate();
			blur.shader.render(tracker.getLastDuration());
		}

		this.client.getProfiler().pop();
	}

	@Inject(method = "bobView",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"),
		cancellable = true)
	private void axolotlclient$minimalViewBob(MatrixStack matrices, float tickDelta, CallbackInfo ci,
											  @Local(ordinal = 2) float g, @Local(ordinal = 3) float h) {
		if (AxolotlClient.CONFIG.minimalViewBob.get()) {
			g /= 2;
			h /= 2;
			matrices.translate(MathHelper.sin(g * (float) Math.PI) * h * 0.5F,
				-Math.abs(MathHelper.cos(g * (float) Math.PI) * h), 0.0F
			);
			matrices.multiply(
				Axis.Z_POSITIVE.rotationDegrees(MathHelper.sin(g * (float) Math.PI) * h * 3.0F).get(new Matrix4f()));
			matrices.multiply(
				Axis.X_POSITIVE.rotationDegrees(Math.abs(MathHelper.cos(g * (float) Math.PI - 0.2F) * h) * 5.0F)
					.get(new Matrix4f()));
			ci.cancel();
		}
	}

	@Inject(method = "bobViewWhenHurt", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/MinecraftClient;getCameraEntity()Lnet/minecraft/entity/Entity;"),
		cancellable = true)
	private void axolotlclient$noHurtCam(MatrixStack matrixStack, float f, CallbackInfo ci) {
		if (AxolotlClient.CONFIG.noHurtCam.get()) {
			ci.cancel();
		}
	}
}
