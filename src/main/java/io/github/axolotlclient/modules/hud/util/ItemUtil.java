package io.github.axolotlclient.modules.hud.util;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class ItemUtil {

	public static int getTotal(MinecraftClient client, ItemStack stack) {
		List<ItemStack> item = ItemUtil.getItems(client);
		if (item == null) {
			return 0;
		}
		AtomicInteger count = new AtomicInteger();
		item.forEach(itemStack -> {
			if (itemStack != null && itemStack.getItem() == stack.getItem()){
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
		//items.addAll(client.player.inventory.)
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
	public static ArrayList<ItemUtil.TimedItemStorage> removeOld(List<ItemUtil.TimedItemStorage> list, int time) {
		ArrayList<ItemUtil.TimedItemStorage> stored = new ArrayList<>();
		for (ItemUtil.TimedItemStorage storage : list) {
			if (storage.getPassedTime() <= time) {
				stored.add(storage);
			}
		}
		return stored;
	}
	public static Optional<ItemUtil.TimedItemStorage> getTimedItemFromItem(ItemStack item, List<ItemUtil.TimedItemStorage> list) {
		ItemStack compare = item.copy();
		compare.count=1;
		for (ItemUtil.TimedItemStorage storage : list) {
			if (isEqual (storage.stack, compare)) {
				return Optional.of(storage);
			}
		}
		return Optional.empty();
	}
	public static Optional<ItemUtil.ItemStorage> getItemFromItem(ItemStack item, List<ItemUtil.ItemStorage> list) {
		ItemStack compare = item.copy();
		compare.count=1;
		for (ItemUtil.ItemStorage storage : list) {
			if (isEqual(storage.stack, compare)) {
				return Optional.of(storage);
			}
		}
		return Optional.empty();
	}
	private static boolean isEqual(ItemStack stack, ItemStack compare){
		return stack != null && compare != null && stack.getItem() == compare.getItem();
	}
	public static List<ItemStorage> storageFromItem(List<ItemStack> items) {
		ArrayList<ItemStorage> storage = new ArrayList<>();
		for (ItemStack item : items) {
			if (item ==null) {
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

	// Minecraft has decided to not use matrixstack's in their itemrender class. So this is implementing itemRenderer stuff with matrices.

	public static void renderGuiItemModel(float scale, ItemStack stack, int x, int y) {

		DiffuseLighting.enable();
		GlStateManager.pushMatrix();
		MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides(stack, x+2, y);
		GlStateManager.popMatrix();
	}

	public static void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y,
									 String countLabel, int textColor, boolean shadow) {
		DiffuseLighting.enable();
		GlStateManager.pushMatrix();
		GlStateManager.color4f(textColor >> 24 & 0xff, textColor >> 16 & 0xff, textColor >> 8 & 0xff , textColor & 0xff);
		MinecraftClient.getInstance().getItemRenderer().renderGuiItemOverlay(renderer, stack, x+2, y, null);
		GlStateManager.popMatrix();

	}

	public static class ItemStorage {
		public final ItemStack stack;
		public int times;
		public ItemStorage(ItemStack stack) {
			this(stack, 1);
		}
		public ItemStorage(ItemStack stack, int times) {
			ItemStack copy = stack.copy();
			copy.count =1;
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