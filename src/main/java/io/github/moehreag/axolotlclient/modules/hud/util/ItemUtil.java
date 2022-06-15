package io.github.moehreag.axolotlclient.modules.hud.util;

import com.mojang.blaze3d.lighting.DiffuseLighting;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import io.github.moehreag.axolotlclient.config.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class ItemUtil {

	public static List<ItemStorage> storageFromItem(List<ItemStack> items) {
		ArrayList<ItemStorage> storage = new ArrayList<>();
		for (ItemStack item : items) {
			if (item.isEmpty()) {
				continue;
			}
			Optional<ItemStorage> s = getItemFromItem(item, storage);
			if (s.isPresent()) {
				ItemUtil.ItemStorage store = s.get();
				store.incrementTimes(item.getCount());
			} else {
				storage.add(new ItemUtil.ItemStorage(item, item.getCount()));
			}
		}
		return storage;
	}

	public static List<ItemStack> getItems(MinecraftClient client) {
		ArrayList<ItemStack> items = new ArrayList<>();
		if (client.player == null) {
			return null;
		}
		items.addAll(client.player.getInventory().armor);
		items.addAll(client.player.getInventory().offHand);
		items.addAll(client.player.getInventory().main);
		return items;
	}


	public static ArrayList<ItemUtil.TimedItemStorage> removeOld(List<ItemUtil.TimedItemStorage> list, int time) {
		ArrayList<ItemUtil.TimedItemStorage> stored = new ArrayList<>();
		for (ItemUtil.TimedItemStorage storage : list) {
			if (storage.getPassedTime() <= time) {
				stored.add(storage);
			}
		}
		return stored;
	}

	public static List<ItemUtil.TimedItemStorage> untimedToTimed(List<ItemStorage> list) {
		ArrayList<TimedItemStorage> timed = new ArrayList<>();
		for (ItemStorage stack : list) {
			timed.add(stack.timed());
		}
		return timed;
	}

	public static Optional<ItemUtil.ItemStorage> getItemFromItem(ItemStack item, List<ItemUtil.ItemStorage> list) {
		ItemStack compare = item.copy();
		compare.setCount(1);
		for (ItemUtil.ItemStorage storage : list) {
			if (storage.stack.isItemEqualIgnoreDamage(compare)) {
				return Optional.of(storage);
			}
		}
		return Optional.empty();
	}

	public static Optional<ItemUtil.TimedItemStorage> getTimedItemFromItem(ItemStack item, List<ItemUtil.TimedItemStorage> list) {
		ItemStack compare = item.copy();
		compare.setCount(1);
		for (ItemUtil.TimedItemStorage storage : list) {
			if (storage.stack.isItemEqualIgnoreDamage(compare)) {
				return Optional.of(storage);
			}
		}
		return Optional.empty();
	}

	public static int getTotal(MinecraftClient client, ItemStack stack) {
		List<ItemStack> item = ItemUtil.getItems(client);
		if (item == null || item.isEmpty()) {
			return 0;
		}
		List<ItemUtil.ItemStorage> items = ItemUtil.storageFromItem(item);
		Optional<ItemUtil.ItemStorage> stor = ItemUtil.getItemFromItem(stack, items);
		return stor.map(itemStorage -> itemStorage.times).orElse(0);
	}

	/**
	 * Compares two ItemStorage Lists.
	 * If list1.get(1) is 10, and list2 is 5, it will return 5.
	 * Will return nothing if negative...
	 *
	 * @param list1 one to be based off of
	 * @param list2 one to compare to
	 * @return
	 */
	public static List<ItemStorage> compare(List<ItemStorage> list1, List<ItemStorage> list2) {
		ArrayList<ItemStorage> list = new ArrayList<>();
		for (ItemStorage current : list1) {
			Optional<ItemStorage> optional = getItemFromItem(current.stack, list2);
			if (optional.isPresent()) {
				ItemStorage other = optional.get();
				if (current.times - other.times <= 0) {
					continue;
				}
				list.add(new ItemStorage(other.stack.copy(), current.times - other.times));
			} else {
				list.add(current.copy());
			}
		}
		return list;
	}

	public static void renderGuiItemModel(MatrixStack matrices, ItemStack stack, float x, float y) {
		MinecraftClient client = MinecraftClient.getInstance();
		BakedModel model = client.getItemRenderer().getHeldItemModel(stack, null, client.player, (int) (x * y));
		client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
		RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.push();
		matrixStack.translate((double)x, (double)y, (double)(100.0F + MinecraftClient.getInstance().getItemRenderer().zOffset));
		matrixStack.translate(8.0, 8.0, 0.0);
		matrixStack.scale(1.0F, -1.0F, 1.0F);
		matrixStack.scale(16.0F, 16.0F, 16.0F);
		RenderSystem.applyModelViewMatrix();
		MatrixStack matrixStack2 = new MatrixStack();
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		boolean bl = !model.isSideLit();
		if (bl) {
			DiffuseLighting.setupFlatGuiLighting();
		}

		MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack2, immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
		immediate.draw();
		RenderSystem.enableDepthTest();
		if (bl) {
			DiffuseLighting.setup3DGuiLighting();
		}

		matrixStack.pop();
		RenderSystem.applyModelViewMatrix();
	}

	public static void renderGuiItemOverlay(MatrixStack matrices, TextRenderer renderer, ItemStack stack, int x, int y,
	                                        String countLabel, int textColor, boolean shadow) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (stack.isEmpty()) {
			return;
		}

		if (stack.getCount() != 1 || countLabel != null) {
			String string = countLabel == null ? String.valueOf(stack.getCount()) :
				countLabel;
			matrices.translate(0.0, 0.0, client.getItemRenderer().zOffset + 200.0F);
			DrawUtil.drawString(matrices, renderer, string, (x + 19 - 2 - renderer.getWidth(string)),
				y + 6 + 3,
				textColor, shadow);
		}

		if (stack.isItemBarVisible()) {
			RenderSystem.disableDepthTest();
			RenderSystem.disableTexture();
			RenderSystem.disableBlend();
			int i = stack.getItemBarStep();
			int j = stack.getItemBarColor();
			DrawUtil.fillRect(matrices, new Rectangle(x + 2, y + 13, 13, 2), Color.BLACK);
			DrawUtil.fillRect(matrices, new Rectangle(x + 2, y + 13, i, 1), new Color(j >> 16 & 255, j >> 8 & 255,
				j & 255,
				255));
			RenderSystem.enableBlend();
			RenderSystem.enableTexture();
			RenderSystem.enableDepthTest();
		}

		ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
		float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), MinecraftClient.getInstance().getTickDelta());
		if (f > 0.0F) {
			RenderSystem.disableDepthTest();
			RenderSystem.disableTexture();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			DrawUtil.fillRect(matrices, new Rectangle(x, y + MathHelper.floor(16.0F * (1.0F - f)), 16,
				MathHelper.ceil(16.0F * f)), Color.WHITE.withAlpha(127));
			RenderSystem.enableTexture();
			RenderSystem.enableDepthTest();
		}

	}

	// Minecraft has decided to not use matrixstack's in their itemrender class. So this is implementing itemRenderer stuff with matrices.

	public void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		buffer.vertex(x, y, 0.0D).color(red, green, blue, alpha).next();
		buffer.vertex(x, y + height, 0.0D).color(red, green, blue, alpha).next();
		buffer.vertex(x + width, y + height, 0.0D).color(red, green, blue, alpha).next();
		buffer.vertex(x + width, y, 0.0D).color(red, green, blue, alpha).next();
		Tessellator.getInstance().draw();
	}

	public static class ItemStorage {
		public final ItemStack stack;
		public int times;

		public ItemStorage(ItemStack stack) {
			this(stack, 1);
		}

		public ItemStorage(ItemStack stack, int times) {
			ItemStack copy = stack.copy();
			copy.setCount(1);
			this.stack = copy;
			this.times = times;
		}


		public void incrementTimes(int num) {
			times = times + num;
		}

		public ItemStorage copy() {
			return new ItemStorage(stack.copy(), times);
		}

		public TimedItemStorage timed() {
			return new TimedItemStorage(stack, times);
		}

	}

	public static class TimedItemStorage extends ItemStorage {
		public float start;

		public TimedItemStorage(ItemStack stack) {
			this(stack, 1);
		}

		public TimedItemStorage(ItemStack stack, int times) {
			super(stack, times);
			this.start = Util.getMeasuringTimeMs();
		}

		public float getPassedTime() {
			return Util.getMeasuringTimeMs() - start;
		}

		@Override
		public void incrementTimes(int num) {
			super.incrementTimes(num);
			refresh();
		}

		public void refresh() {
			start = Util.getMeasuringTimeMs();
		}


	}
}
