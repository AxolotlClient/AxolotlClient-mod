package io.github.axolotlclient.modules.zoom;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.FloatOption;
import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.util.Util;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;


//Based on https://github.com/LogicalGeekBoy/logical_zoom/blob/master/src/main/java/com/logicalgeekboy/logical_zoom/LogicalZoom.java
public class Zoom extends AbstractModule {

	public static boolean active;
	private static Double originalSensitivity;
	private static boolean originalSmoothCamera;
	public static KeyBinding keyBinding;
	private static double targetFactor = 1;
	private static double divisor;
	private static float lastAnimatedFactor = 1;
	private static float animatedFactor = 1;
	private static double lastReturnedFov;

	public static final FloatOption zoomDivisor = new FloatOption("zoomDivisor", 1F, 16F, 4F);
	public static final FloatOption zoomSpeed = new FloatOption("zoomSpeed", 1F, 10F, 7.5F);
	public static final BooleanOption zoomScrolling = new BooleanOption("zoomScrolling", false);
	public static final BooleanOption decreaseSensitivity = new BooleanOption("decreaseSensitivity", true);
	public static final BooleanOption smoothCamera = new BooleanOption("smoothCamera", false);
	public static final IntegerOption zoomScrollStep = new IntegerOption("zoomScrollStep", 5, 1, 20);

	public final OptionCategory zoom = new OptionCategory("zoom");

	public static final Identifier ID = new Identifier("zoom");

	@Override
	public void init() {
		zoom.add(zoomDivisor);
		zoom.add(zoomSpeed);
		zoom.add(zoomScrolling);
		zoom.add(zoomScrollStep);
		zoom.add(decreaseSensitivity);
		zoom.add(smoothCamera);

		AxolotlClient.CONFIG.rendering.addSubCategory(zoom);

		keyBinding = new KeyBinding("key.zoom", GLFW.GLFW_KEY_C, "category.axolotlclient");
		KeyBindingHelper.registerKeyBinding(keyBinding);

		active = false;
	}

	private static boolean keyHeld() {
		return keyBinding.isPressed();
	}

	public static double getFov(double current, float tickDelta) {
		double result = current * (zoomSpeed.get() == 10 ? targetFactor
			: Util.lerp(lastAnimatedFactor, animatedFactor, tickDelta));



		if (lastReturnedFov != 0 && lastReturnedFov != result) {
			MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();
		}
		lastReturnedFov = result;

		return result;
	}

	public static void setOptions() {
		originalSensitivity = MinecraftClient.getInstance().options.mouseSensitivity;

		if (smoothCamera.get()) {
			originalSmoothCamera = MinecraftClient.getInstance().options.smoothCameraEnabled;
			MinecraftClient.getInstance().options.smoothCameraEnabled = true;
		}

		updateSensitivity();
	}

	private static void updateSensitivity() {
		if (decreaseSensitivity.get()) {
			MinecraftClient.getInstance().options.mouseSensitivity = (originalSensitivity / (divisor*divisor));
		}
	}

	public static void restoreOptions() {
		MinecraftClient.getInstance().options.mouseSensitivity = originalSensitivity;
		MinecraftClient.getInstance().options.smoothCameraEnabled = originalSmoothCamera;
	}

	public static void update() {
		if(shouldStart()) {
			start();
		} else if(shouldStop()) {
			stop();
		}
	}

	public static boolean scroll(double amount) {
		if (active && zoomScrolling.get() && amount != 0) {
			setDivisor(Math.max(1, divisor + (amount*zoomScrollStep.get() / Math.abs(amount))));
			updateSensitivity();
			return true;
		}

		return false;
	}

	public void tick() {
		lastAnimatedFactor = animatedFactor;
		animatedFactor += (targetFactor - animatedFactor) * (zoomSpeed.get() / 10F);
	}

	private static boolean shouldStart() {
		return keyHeld() && !active;
	}

	private static boolean shouldStop() {
		return !keyHeld() && active;
	}

	private static void setDivisor(double value) {
		divisor = value;
		targetFactor = 1F / value;
	}

	private static void start() {
		active = true;
		setDivisor(zoomDivisor.get());
		setOptions();
	}

	private static void stop() {
		active = false;
		targetFactor = 1;
		restoreOptions();
	}

}
