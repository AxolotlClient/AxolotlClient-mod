/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.modules.hud.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityItemStackRenderHelper;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

@UtilityClass
public class ItemUtil {

	private static final Identifier ITEM_GLINT_TEXTURE = new Identifier("textures/misc/enchanted_item_glint.png");

	public static int getTotal(MinecraftClient client, ItemStack stack) {
		List<ItemStack> item = ItemUtil.getItems(client);
		if (item == null) {
			return 0;
		}
		AtomicInteger count = new AtomicInteger();
		item.forEach(itemStack -> {
			if (itemStack != null && stack != null && itemStack.getItem() == stack.getItem()) {
				count.addAndGet(itemStack.count);
			}
		});
		return count.get();
	}

	public static List<ItemStack> getItems(MinecraftClient client) {
		ArrayList<ItemStack> items = new ArrayList<>();
		if (client.player == null) {
			return null;
		}
		items.addAll(Arrays.asList(client.player.inventory.armor));
		items.addAll(Arrays.asList(client.player.inventory.main));
		return items;
	}

	/**
	 * Compares two ItemStorage Lists.
	 * If list1.get(1) is 10, and list2 is 5, it will return 5.
	 * Will return nothing if negative...
	 *
	 * @param list1 one to be based off of
	 * @param list2 one to compare to
	 * @return the item storage
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

	public static Optional<ItemUtil.ItemStorage> getItemFromItem(ItemStack item, List<ItemUtil.ItemStorage> list) {
		ItemStack compare = item.copy();
		compare.count = 1;
		for (ItemUtil.ItemStorage storage : list) {
			if (isEqual(storage.stack, compare)) {
				return Optional.of(storage);
			}
		}
		return Optional.empty();
	}

	private static boolean isEqual(ItemStack stack, ItemStack compare) {
		return stack != null && compare != null && stack.getItem() == compare.getItem();
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

	public static Optional<ItemUtil.TimedItemStorage> getTimedItemFromItem(ItemStack item,
																		   List<ItemUtil.TimedItemStorage> list) {
		ItemStack compare = item.copy();
		compare.count = 1;
		for (ItemUtil.TimedItemStorage storage : list) {
			if (isEqual(storage.stack, compare)) {
				return Optional.of(storage);
			}
		}
		return Optional.empty();
	}

	public static List<ItemStorage> storageFromItem(List<ItemStack> items) {
		ArrayList<ItemStorage> storage = new ArrayList<>();
		for (ItemStack item : items) {
			if (item == null) {
				continue;
			}
			Optional<ItemStorage> s = getItemFromItem(item, storage);
			if (s.isPresent()) {
				ItemUtil.ItemStorage store = s.get();
				store.incrementTimes(item.count);
			} else {
				storage.add(new ItemUtil.ItemStorage(item, item.count));
			}
		}
		return storage;
	}

	public static List<ItemUtil.TimedItemStorage> untimedToTimed(List<ItemStorage> list) {
		ArrayList<TimedItemStorage> timed = new ArrayList<>();
		for (ItemStorage stack : list) {
			timed.add(stack.timed());
		}
		return timed;
	}

	// The scaling stuff wasn't a problem on 1.8.9 so no need to create more complicated stuff

	public static void renderGuiItemModel(ItemStack stack, int x, int y) {
		DiffuseLighting.enable();
		GlStateManager.pushMatrix();
		MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides(stack, x, y);
		GlStateManager.popMatrix();
		DiffuseLighting.disable();
	}

	public static void renderColoredGuiItemModel(ItemStack stack, int x, int y, Color color){
		DiffuseLighting.enable();
		GlStateManager.pushMatrix();

		MinecraftClient client = MinecraftClient.getInstance();

		if (stack != null && stack.getItem() != null) {
			client.getItemRenderer().zOffset += 50.0F;


			BakedModel bakedModel = client.getItemRenderer().getModels().getModel(stack);
			GlStateManager.pushMatrix();
			client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableAlphaTest();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);


			GlStateManager.translate((float)x, (float)y, 100.0F + client.getItemRenderer().zOffset);
			GlStateManager.translate(8.0F, 8.0F, 0.0F);
			GlStateManager.scale(1.0F, 1.0F, -1.0F);
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			if (bakedModel.hasDepth()) {
				GlStateManager.scale(40.0F, 40.0F, 40.0F);
				GlStateManager.rotate(210.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.enableLighting();
			} else {
				GlStateManager.scale(64.0F, 64.0F, 64.0F);
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.disableLighting();
			}


			bakedModel.getTransformation().apply(ModelTransformation.Mode.GUI);


			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			if (bakedModel.isBuiltin()) {
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(-0.5F, -0.5F, -0.5F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableRescaleNormal();
				BlockEntityItemStackRenderHelper.INSTANCE.renderItem(stack);
			} else {
				GlStateManager.translate(-0.5F, -0.5F, -0.5F);
				renderBakedItemModel(bakedModel, color.getAsInt(), stack);
				if (stack.hasEnchantmentGlint()) {
					renderGlint(bakedModel);
				}
			}

			GlStateManager.popMatrix();


			GlStateManager.disableAlphaTest();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableLighting();
			GlStateManager.popMatrix();
			client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pop();


			client.getItemRenderer().zOffset -= 50.0F;
		}

		GlStateManager.popMatrix();
		DiffuseLighting.disable();
	}

	private void renderBakedItemModel(BakedModel bakedModel, int color, ItemStack itemStack) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.BLOCK_NORMALS);

		for(Direction direction : Direction.values()) {
			renderBakedItemQuads(bufferBuilder, bakedModel.getByDirection(direction), color, itemStack);
		}

		renderBakedItemQuads(bufferBuilder, bakedModel.getQuads(), color, itemStack);
		tessellator.draw();
	}

	private void renderBakedItemQuads(BufferBuilder bufferBuilder, List<BakedQuad> list, int color, ItemStack itemStack) {
		boolean bl = color == -1 && itemStack != null;
		int j = 0;

		for(int k = list.size(); j < k; ++j) {
			BakedQuad bakedQuad = list.get(j);
			int l = color;
			if (bl && bakedQuad.hasColor()) {
				l = itemStack.getItem().getDisplayColor(itemStack, bakedQuad.getColorIndex());
				if (GameRenderer.anaglyphEnabled) {
					l = TextureUtil.getAnaglyphColor(l);
				}

				l |= -16777216;
			}

			renderQuad(bufferBuilder, bakedQuad, l);
		}
	}

	private void renderGlint(BakedModel bakedModel) {
		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(768, 1);
		MinecraftClient.getInstance().getTextureManager().bindTexture(ITEM_GLINT_TEXTURE);
		GlStateManager.matrixMode(5890);
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f = (float)(MinecraftClient.getTime() % 3000L) / 3000.0F / 8.0F;
		GlStateManager.translate(f, 0.0F, 0.0F);
		GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
		renderBakedItemModel(bakedModel, -8372020, null);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float g = (float)(MinecraftClient.getTime() % 4873L) / 4873.0F / 8.0F;
		GlStateManager.translate(-g, 0.0F, 0.0F);
		GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
		renderBakedItemModel(bakedModel, -8372020, null);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.blendFunc(770, 771);
		GlStateManager.enableLighting();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
	}

	private void renderQuad(BufferBuilder bufferBuilder, BakedQuad bakedQuad, int color) {
		bufferBuilder.putArray(bakedQuad.getVertexData());
		bufferBuilder.putQuadColor(color);
		Vec3i vec3i = bakedQuad.getFace().getVector();
		bufferBuilder.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
	}


	public static void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel,
											int textColor, boolean shadow) {
		DiffuseLighting.enable();
		GlStateManager.pushMatrix();
		GlStateManager.color(textColor >> 24 & 0xff, textColor >> 16 & 0xff, textColor >> 8 & 0xff, textColor & 0xff);
		if (stack != null) {
			if (stack.count != 1 || countLabel != null) {
				String string = countLabel == null ? String.valueOf(stack.count) : countLabel;
				if (countLabel == null && stack.count < 1) {
					string = Formatting.RED + String.valueOf(stack.count);
				}

				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableBlend();
				renderer.draw(string, (float) (x + 19 - 2 - renderer.getStringWidth(string)), (float) (y + 6 + 3),
					16777215, shadow);
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}

			if (stack.isDamaged()) {
				int i = (int) Math.round(13.0 - (double) stack.getDamage() * 13.0 / (double) stack.getMaxDamage());
				int j = (int) Math.round(255.0 - (double) stack.getDamage() * 255.0 / (double) stack.getMaxDamage());
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableTexture();
				GlStateManager.disableAlphaTest();
				GlStateManager.disableBlend();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				renderGuiQuad(bufferBuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
				renderGuiQuad(bufferBuilder, x + 2, y + 13, 12, 1, (255 - j) / 4, 64, 0, 255);
				renderGuiQuad(bufferBuilder, x + 2, y + 13, i, 1, 255 - j, j, 0, 255);
				GlStateManager.enableBlend();
				GlStateManager.enableAlphaTest();
				GlStateManager.enableTexture();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}
		}

		DiffuseLighting.disable();
		GlStateManager.popMatrix();
	}

	private static void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green,
									  int blue, int alpha) {
		buffer.begin(7, VertexFormats.POSITION_COLOR);
		buffer.vertex(x, y, 0.0).color(red, green, blue, alpha).next();
		buffer.vertex(x, y + height, 0.0).color(red, green, blue, alpha).next();
		buffer.vertex(x + width, y + height, 0.0).color(red, green, blue, alpha).next();
		buffer.vertex(x + width, y, 0.0).color(red, green, blue, alpha).next();
		Tessellator.getInstance().draw();
	}

	public static class ItemStorage {

		public final ItemStack stack;
		public int times;

		public ItemStorage(ItemStack stack, int times) {
			ItemStack copy = stack.copy();
			copy.count = 1;
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

		public TimedItemStorage(ItemStack stack, int times) {
			super(stack, times);
			this.start = MinecraftClient.getTime();
		}

		public float getPassedTime() {
			return MinecraftClient.getTime() - start;
		}

		@Override
		public void incrementTimes(int num) {
			super.incrementTimes(num);
			refresh();
		}

		public void refresh() {
			start = MinecraftClient.getTime();
		}
	}
}
