package io.github.moehreag.axolotlclient.modules.hud.util;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class ItemUtil {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void renderGuiItem(ItemStack itemStack, int x, int y, Color color){
        //GlStateManager.enableBlend();
        GlStateManager.enableLighting();
        GuiLighting.enable();
        MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides(itemStack, x+2, y);
        GuiLighting.disable();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if(itemStack.count>1) {
            client.textRenderer.drawWithShadow(String.valueOf(itemStack.count),
                    (float) (x + 2 + 19 - 2 - client.textRenderer.getStringWidth(String.valueOf(itemStack.count))),
                    (float) (y + 6 + 3),
                    color != null ? color.color : 16777215);
        }
        //GlStateManager.disableBlend();
        GlStateManager.enableDepthTest();
    }
/*
    public static List<ItemStorage> storageFromItem(List<ItemStack> items) {
        ArrayList<ItemStorage> storage = new ArrayList<>();
        for (ItemStack item : items) {
            if (item.isEmpty()) {
                continue;
            }
            Optional<ItemStorage> s = getItemFromItem(item, storage);
            if (s.isPresent()) {
                ItemStorage store = s.get();
                store.incrementTimes(item.count);
            } else {
                storage.add(new ItemStorage(item, item.count));
            }
        }
        return storage;
    }

    public static List<ItemStack> getItems(MinecraftClient client) {
        ArrayList<ItemStack> items = new ArrayList<>();
        if (client.player == null) {
            return null;
        }
        items.addAll(Arrays.asList(client.player.inventory.armor));
        //items.addAll(client.player.inventory.offHand);
        items.addAll(Arrays.asList(client.player.inventory.main));
        return items;
    }


    public ArrayList<TimedItemStorage> removeOld(List<TimedItemStorage> list, int time) {
        ArrayList<TimedItemStorage> stored = new ArrayList<>();
        for (TimedItemStorage storage : list) {
            if (storage.getPassedTime() <= time) {
                stored.add(storage);
            }
        }
        return stored;
    }

    public static List<TimedItemStorage> untimedToTimed(List<ItemStorage> list) {
        ArrayList<TimedItemStorage> timed = new ArrayList<>();
        for (ItemStorage stack : list) {
            timed.add(stack.timed());
        }
        return timed;
    }

    public static Optional<ItemStorage> getItemFromItem(ItemStack item, List<ItemStorage> list) {
        ItemStack compare = item.copy();
        compare.count = 1;
        for (ItemStorage storage : list) {
            if (ItemStack.equalsIgnoreDamage(storage.stack, compare)) {
                return Optional.of(storage);
            }
        }
        return Optional.empty();
    }

    public Optional<TimedItemStorage> getTimedItemFromItem(ItemStack item, List<TimedItemStorage> list) {
        ItemStack compare = item.copy();
        compare.count =1;
        for (TimedItemStorage storage : list) {
            if (ItemStack.equalsIgnoreDamage(storage.stack, compare)) {
                return Optional.of(storage);
            }
        }
        return Optional.empty();
    }

    public int getTotal(MinecraftClient client, ItemStack stack) {
        List<ItemStack> item = ItemUtil.getItems(client);
        if (item == null || item.isEmpty()) {
            return 0;
        }
        List<ItemStorage> items = ItemUtil.storageFromItem(item);
        Optional<ItemStorage> stor = ItemUtil.getItemFromItem(stack, items);
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
    /*public List<ItemStorage> compare(List<ItemStorage> list1, List<ItemStorage> list2) {
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

    public static void renderGuiItemModel(ItemStack stack, float x, float y) {
        MinecraftClient client = MinecraftClient.getInstance();
        BakedModel model = client.getItemRenderer().getModels().method_9884(stack);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, VertexFormats.POSITION_TEXTURE);
        GlStateManager.bindTexture(client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).getGlId());//.setFilter(false, false);
        //GlStateManager.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        GlStateManager.enableBlend();
        //GlStateManager.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        //GlStateManager.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, (100.0F + client.getItemRenderer().zOffset));
        GlStateManager.translated(8.0D, 8.0D, 0.0D);
        GlStateManager.scalef(1.0F, -1.0F, 1.0F);
        GlStateManager.scalef(16.0F, 16.0F, 16.0F);
        //GlStateManager.applyModelViewMatrix();
        //VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        /*boolean bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }*/

        //client.getItemRenderer().renderItem(stack, 15728880, model);
     /*   tessellator.draw();
        GlStateManager.enableDepthTest();
        /*if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }

        GlStateManager.popMatrix();
        //GlStateManager.applyModelViewMatrix();
    }

    public static void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y,
                                            String countLabel, int textColor, boolean shadow) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (stack.isEmpty()) {
            return;
        }

        if (stack.count != 1 || countLabel != null) {
            String string = countLabel == null ? String.valueOf(stack.count) :
                    countLabel;
            //matrices.translate(0.0, 0.0, client.getItemRenderer().zOffset + 200.0F);
            DrawUtil.drawString(renderer, string, x + 19 - 2 - renderer.getStringWidth(string),
                    y + 6 + 3,
                    textColor, shadow);
        }

        //if (stack.isItemBarVisible()) {
            /*GlStateManager.disableDepthTest();
            GlStateManager.disableTexture();
            GlStateManager.disableBlend();
            int i = stack.getItemBarStep();
            int j = stack.getItemBarColor();
            DrawUtil.fillRect(new Rectangle(x + 2, y + 13, 13, 2), Color.BLACK);
            DrawUtil.fillRect(new Rectangle(x + 2, y + 13, i, 1), new Color(j >> 16 & 255, j >> 8 & 255,
                    j & 255,
                    255));
            GlStateManager.enableBlend();
            GlStateManager.enableTexture();
            GlStateManager.enableDepthTest();
        //}

        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
        float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getStackInHand().getCooldownProgress(stack.getItem(), MinecraftClient.getInstance().getTickDelta());
        if (f > 0.0F) {
            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture();
            GlStateManager.enableBlend();
            //GlStateManager.blendFunc();
            DrawUtil.fillRect(new Rectangle(x, y + MathHelper.floor(16.0F * (1.0F - f)), 16,
                MathHelper.ceil(16.0F * f)), Color.WHITE.withAlpha(127));
            GlStateManager.enableTexture();
            GlStateManager.enableDepthTest();
        }*/

    /*}

    // Minecraft has decided to not use matrixstack's in their itemrender class. So this is implementing itemRenderer stuff with matrices.

    public void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        buffer.begin(7, VertexFormats.POSITION_COLOR);
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


    }*/
}
